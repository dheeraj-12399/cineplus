import React, { useState, useEffect } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { tmdbApi, tmdbImage } from '../services/tmdbApi';
import { MultiSearchResult } from '../types/tmdb';
import { MediaCard } from '../components/MediaCard';
import { Search, Loader2, User, Film, Tv, Star } from 'lucide-react';

export const SearchPage: React.FC = () => {
  const [searchParams, setSearchParams] = useSearchParams();
  const initialQuery = searchParams.get('q') || '';

  const [query, setQuery] = useState(initialQuery);
  const [results, setResults] = useState<MultiSearchResult[]>([]);
  const [filterType, setFilterType] = useState<'all' | 'movie' | 'tv' | 'person'>('all');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const quickSearches = ['Salaar', 'Kalki 2898 AD', 'Devara', 'RRR', 'Pushpa', 'Prabhas', 'Dhootha', 'Stree 2'];

  const performSearch = async (searchTerm: string) => {
    if (!searchTerm.trim()) {
      setResults([]);
      return;
    }
    setLoading(true);
    try {
      const res = await tmdbApi.searchMulti(searchTerm.trim(), 1);
      setResults(res.results || []);
    } catch (err) {
      console.error('Search failed:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (initialQuery) {
      performSearch(initialQuery);
    }
  }, [initialQuery]);

  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (query.trim()) {
      setSearchParams({ q: query.trim() });
      performSearch(query.trim());
    }
  };

  const handleQuickTagClick = (tag: string) => {
    setQuery(tag);
    setSearchParams({ q: tag });
    performSearch(tag);
  };

  const filteredResults = results.filter(item => {
    if (filterType === 'all') return true;
    return item.media_type === filterType;
  });

  return (
    <div className="min-h-screen bg-[#0B0F19] pt-24 pb-20">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 space-y-6">
        {/* Search Header Form */}
        <div className="max-w-3xl mx-auto space-y-4">
          <form onSubmit={handleSearchSubmit} className="relative">
            <input
              type="text"
              placeholder="Search by title (e.g. Salaar), actor, or director..."
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              className="w-full bg-[#141A29] border border-white/15 text-white text-base sm:text-lg rounded-2xl pl-12 pr-28 py-3.5 focus:outline-none focus:border-[#E50914] shadow-2xl transition-all placeholder-gray-500"
            />
            <Search className="w-6 h-6 text-gray-400 absolute left-4 top-4" />
            <button
              type="submit"
              className="absolute right-2.5 top-2.5 bg-[#E50914] hover:bg-[#F40612] text-white font-bold px-5 py-2 rounded-xl text-xs uppercase tracking-wider transition-colors"
            >
              Search
            </button>
          </form>

          {/* Quick Trending Suggestions */}
          <div className="flex items-center space-x-2 text-xs overflow-x-auto no-scrollbar py-1">
            <span className="text-gray-400 font-semibold whitespace-nowrap">Trending Searches:</span>
            {quickSearches.map(tag => (
              <button
                key={tag}
                onClick={() => handleQuickTagClick(tag)}
                className="bg-[#141A29] hover:bg-[#1E273D] text-[#00E5FF] hover:text-white border border-white/10 px-3 py-1 rounded-full whitespace-nowrap text-xs transition-colors"
              >
                {tag}
              </button>
            ))}
          </div>

          {/* Filter Tabs */}
          {results.length > 0 && (
            <div className="flex items-center space-x-2 pt-2">
              {[
                { id: 'all', label: `All (${results.length})` },
                { id: 'movie', label: `Movies (${results.filter(r => r.media_type === 'movie').length})` },
                { id: 'tv', label: `TV Shows (${results.filter(r => r.media_type === 'tv').length})` },
                { id: 'person', label: `People (${results.filter(r => r.media_type === 'person').length})` }
              ].map(tab => (
                <button
                  key={tab.id}
                  onClick={() => setFilterType(tab.id as any)}
                  className={`text-xs font-semibold px-3 py-1.5 rounded-lg transition-colors ${
                    filterType === tab.id
                      ? 'bg-[#E50914] text-white'
                      : 'bg-[#141A29] text-gray-400 hover:text-white'
                  }`}
                >
                  {tab.label}
                </button>
              ))}
            </div>
          )}
        </div>

        {/* Results Area */}
        {loading ? (
          <div className="py-20 flex flex-col items-center justify-center space-y-3">
            <Loader2 className="w-10 h-10 text-[#E50914] animate-spin" />
            <p className="text-gray-400 text-sm">Searching real TMDB database...</p>
          </div>
        ) : query && filteredResults.length === 0 ? (
          <div className="py-16 text-center space-y-2">
            <p className="text-lg font-bold text-white">No matching titles or people found</p>
            <p className="text-sm text-gray-400">Try checking the spelling or searching for another real movie, show, or actor.</p>
          </div>
        ) : (
          <div className="space-y-8 pt-4">
            {/* Movies & TV Shows Grid */}
            <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-6 gap-3 sm:gap-4">
              {filteredResults.map(item => {
                if (item.media_type === 'person') {
                  return (
                    <div
                      key={`person_${item.id}`}
                      className="bg-[#141A29] border border-white/10 rounded-lg p-3 text-center space-y-2 flex flex-col items-center"
                    >
                      <div className="w-20 h-20 sm:w-24 sm:h-24 rounded-full overflow-hidden bg-gray-800 border-2 border-white/15">
                        <img
                          src={tmdbImage.profile(item.profile_path)}
                          alt={item.name}
                          className="w-full h-full object-cover"
                        />
                      </div>
                      <h4 className="text-xs sm:text-sm font-bold text-white truncate w-full">
                        {item.name}
                      </h4>
                      <span className="text-[10px] text-[#00E5FF] uppercase font-semibold">
                        {item.known_for_department || 'Cast / Crew'}
                      </span>
                    </div>
                  );
                }

                return (
                  <MediaCard
                    key={`${item.media_type}_${item.id}`}
                    item={item as any}
                    mediaType={item.media_type === 'tv' ? 'tv' : 'movie'}
                    widthClass="w-full"
                  />
                );
              })}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
