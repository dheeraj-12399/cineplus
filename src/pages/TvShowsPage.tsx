import React, { useState, useEffect } from 'react';
import { tmdbApi, LANGUAGE_MAP } from '../services/tmdbApi';
import { TvShow } from '../types/tmdb';
import { MediaCard } from '../components/MediaCard';
import { Loader2, Sparkles, Tv } from 'lucide-react';

export const TvShowsPage: React.FC = () => {
  const [selectedLanguage, setSelectedLanguage] = useState<string>('All');
  const [activeTab, setActiveTab] = useState<'trending' | 'popular' | 'top_rated'>('trending');
  const [shows, setShows] = useState<TvShow[]>([]);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(true);
  const [loadingMore, setLoadingMore] = useState(false);

  const fetchShows = async (targetPage = 1, isAppend = false) => {
    if (isAppend) setLoadingMore(true);
    else setLoading(true);

    try {
      let res;
      const langCode = LANGUAGE_MAP[selectedLanguage]?.code || undefined;

      if (langCode) {
        res = await tmdbApi.discoverTv({
          language: langCode,
          page: targetPage,
          sortBy: activeTab === 'top_rated' ? 'vote_average.desc' : 'popularity.desc'
        });
      } else {
        if (activeTab === 'popular') {
          res = await tmdbApi.getPopularTv(targetPage);
        } else if (activeTab === 'top_rated') {
          res = await tmdbApi.getTopRatedTv(targetPage);
        } else {
          res = await tmdbApi.getTrendingTv(targetPage);
        }
      }

      if (isAppend) {
        setShows(prev => {
          const ids = new Set(prev.map(s => s.id));
          const uniqueNew = res.results.filter(s => !ids.has(s.id));
          return [...prev, ...uniqueNew];
        });
      } else {
        setShows(res.results);
      }

      setPage(targetPage);
      setTotalPages(res.total_pages);
    } catch (err) {
      console.error('Failed to load TV shows:', err);
    } finally {
      setLoading(false);
      setLoadingMore(false);
    }
  };

  useEffect(() => {
    fetchShows(1, false);
  }, [selectedLanguage, activeTab]);

  const handleLoadMore = () => {
    if (page < totalPages && !loadingMore) {
      fetchShows(page + 1, true);
    }
  };

  return (
    <div className="min-h-screen bg-[#0B0F19] pt-24 pb-20">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 space-y-6">
        {/* Header */}
        <div className="space-y-1">
          <div className="flex items-center space-x-2">
            <h1 className="text-3xl sm:text-4xl font-black text-white tracking-tight">
              TV Shows & Web Series
            </h1>
            <span className="text-xs bg-[#E50914] text-white px-2 py-0.5 rounded font-bold uppercase tracking-wider">
              Real TMDB
            </span>
          </div>
          <p className="text-gray-400 text-xs sm:text-sm">
            Binge-worthy drama serials, episodic web series, and multi-season sagas.
          </p>
        </div>

        {/* Tab Selector */}
        <div className="flex items-center space-x-2 border-b border-white/10 pb-3">
          {[
            { id: 'trending', label: '🔥 Trending This Week' },
            { id: 'popular', label: '⭐ Most Popular' },
            { id: 'top_rated', label: '🏆 Highest Rated' }
          ].map(tab => (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id as any)}
              className={`text-xs sm:text-sm font-bold px-4 py-2 rounded-lg transition-colors ${
                activeTab === tab.id
                  ? 'bg-[#E50914] text-white shadow'
                  : 'text-gray-400 hover:text-white hover:bg-white/5'
              }`}
            >
              {tab.label}
            </button>
          ))}
        </div>

        {/* Regional Language Filter Chips */}
        <div className="flex flex-wrap gap-2 pt-1">
          {Object.keys(LANGUAGE_MAP).map(lang => {
            const isSelected = selectedLanguage === lang;
            const { flag } = LANGUAGE_MAP[lang];
            return (
              <button
                key={lang}
                onClick={() => setSelectedLanguage(lang)}
                className={`text-xs font-semibold px-3 py-1.5 rounded-full transition-all flex items-center space-x-1.5 ${
                  isSelected
                    ? 'bg-[#00E5FF]/20 text-[#00E5FF] border border-[#00E5FF]/50 font-bold'
                    : 'bg-[#141A29] text-gray-300 hover:bg-[#1E273D] border border-white/10'
                }`}
              >
                <span>{flag}</span>
                <span>{lang}</span>
              </button>
            );
          })}
        </div>

        {/* Shows Grid */}
        {loading ? (
          <div className="py-20 flex flex-col items-center justify-center space-y-3">
            <Loader2 className="w-10 h-10 text-[#E50914] animate-spin" />
            <p className="text-gray-400 text-sm">Fetching real TV series metadata from TMDB...</p>
          </div>
        ) : shows.length === 0 ? (
          <div className="py-20 text-center space-y-3">
            <p className="text-gray-300 font-semibold text-base">No TV series found for this selection.</p>
          </div>
        ) : (
          <>
            <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-6 gap-3 sm:gap-4 pt-2">
              {shows.map(show => (
                <MediaCard key={show.id} item={show} mediaType="tv" widthClass="w-full" />
              ))}
            </div>

            {/* Load More Pagination */}
            {page < totalPages && (
              <div className="pt-10 flex justify-center">
                <button
                  onClick={handleLoadMore}
                  disabled={loadingMore}
                  className="flex items-center space-x-2 bg-[#141A29] hover:bg-[#1E273D] border border-white/15 text-white font-bold px-8 py-3 rounded-full transition-all hover:scale-105 shadow-lg text-sm disabled:opacity-50"
                >
                  {loadingMore ? (
                    <>
                      <Loader2 className="w-4 h-4 animate-spin text-[#00E5FF]" />
                      <span>Loading Next Page...</span>
                    </>
                  ) : (
                    <>
                      <Sparkles className="w-4 h-4 text-[#FFD700]" />
                      <span>Load More Shows (Page {page + 1} of {totalPages})</span>
                    </>
                  )}
                </button>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
};
