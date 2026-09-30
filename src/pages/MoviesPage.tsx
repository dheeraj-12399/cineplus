import React, { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import { tmdbApi, LANGUAGE_MAP, GENRE_MAP } from '../services/tmdbApi';
import { Movie } from '../types/tmdb';
import { MediaCard } from '../components/MediaCard';
import { Filter, Loader2, Sparkles } from 'lucide-react';

export const MoviesPage: React.FC = () => {
  const [searchParams, setSearchParams] = useSearchParams();
  const initialLang = searchParams.get('lang') || 'All';

  const [selectedLanguage, setSelectedLanguage] = useState<string>(initialLang);
  const [selectedGenre, setSelectedGenre] = useState<string>('All');
  const [movies, setMovies] = useState<Movie[]>([]);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(true);
  const [loadingMore, setLoadingMore] = useState(false);

  useEffect(() => {
    const langFromUrl = searchParams.get('lang');
    if (langFromUrl && langFromUrl !== selectedLanguage) {
      setSelectedLanguage(langFromUrl);
    }
  }, [searchParams]);

  const fetchMovies = async (targetPage = 1, isAppend = false) => {
    if (isAppend) setLoadingMore(true);
    else setLoading(true);

    try {
      const langConfig = LANGUAGE_MAP[selectedLanguage];
      const langCode = langConfig?.code || undefined;
      const genreId = selectedGenre !== 'All' ? GENRE_MAP[selectedGenre] : undefined;

      const res = await tmdbApi.discoverMovies({
        language: langCode,
        genreId: genreId,
        page: targetPage,
        sortBy: 'popularity.desc'
      });

      if (isAppend) {
        setMovies(prev => {
          const ids = new Set(prev.map(m => m.id));
          const uniqueNew = res.results.filter(m => !ids.has(m.id));
          return [...prev, ...uniqueNew];
        });
      } else {
        setMovies(res.results);
      }

      setPage(targetPage);
      setTotalPages(res.total_pages);
    } catch (err) {
      console.error('Failed to load movies:', err);
    } finally {
      setLoading(false);
      setLoadingMore(false);
    }
  };

  useEffect(() => {
    fetchMovies(1, false);
  }, [selectedLanguage, selectedGenre]);

  const handleLanguageChange = (lang: string) => {
    setSelectedLanguage(lang);
    if (lang === 'All') {
      searchParams.delete('lang');
      setSearchParams(searchParams);
    } else {
      setSearchParams({ lang });
    }
  };

  const handleLoadMore = () => {
    if (page < totalPages && !loadingMore) {
      fetchMovies(page + 1, true);
    }
  };

  return (
    <div className="min-h-screen bg-[#0B0F19] pt-24 pb-20">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 space-y-6">
        {/* Page Header */}
        <div className="space-y-1">
          <div className="flex items-center space-x-2">
            <h1 className="text-3xl sm:text-4xl font-black text-white tracking-tight">
              {selectedLanguage !== 'All' ? `${selectedLanguage} Movies` : 'Browse Movies'}
            </h1>
            <span className="text-xs bg-[#E50914] text-white px-2 py-0.5 rounded font-bold uppercase tracking-wider">
              TMDB Live
            </span>
          </div>
          <p className="text-gray-400 text-xs sm:text-sm">
            Dynamically browsing thousands of real movie titles with full production credits and watch availability.
          </p>
        </div>

        {/* Language Filter Chips */}
        <div className="space-y-2">
          <span className="text-xs font-semibold text-gray-400 uppercase tracking-wider flex items-center space-x-1">
            <Filter className="w-3.5 h-3.5 text-[#00E5FF]" />
            <span>Select Regional Cinema</span>
          </span>
          <div className="flex flex-wrap gap-2">
            {Object.keys(LANGUAGE_MAP).map(lang => {
              const isSelected = selectedLanguage === lang;
              const { flag } = LANGUAGE_MAP[lang];
              return (
                <button
                  key={lang}
                  onClick={() => handleLanguageChange(lang)}
                  className={`text-xs font-semibold px-3.5 py-1.5 rounded-full transition-all flex items-center space-x-1.5 ${
                    isSelected
                      ? 'bg-[#E50914] text-white shadow-md shadow-red-900/40 border border-[#E50914]'
                      : 'bg-[#141A29] text-gray-300 hover:bg-[#1E273D] border border-white/10'
                  }`}
                >
                  <span>{flag}</span>
                  <span>{lang === 'All' ? 'All Languages' : lang}</span>
                </button>
              );
            })}
          </div>
        </div>

        {/* Genre Filter Chips */}
        <div className="space-y-2">
          <span className="text-xs font-semibold text-gray-400 uppercase tracking-wider">
            Genre Filter
          </span>
          <div className="flex flex-wrap gap-2">
            {['All', ...Object.keys(GENRE_MAP)].map(genre => {
              const isSelected = selectedGenre === genre;
              return (
                <button
                  key={genre}
                  onClick={() => setSelectedGenre(genre)}
                  className={`text-xs font-medium px-3 py-1 rounded-md transition-all ${
                    isSelected
                      ? 'bg-[#00E5FF]/20 text-[#00E5FF] border border-[#00E5FF]/50 font-bold'
                      : 'bg-[#141A29]/70 text-gray-400 hover:text-white border border-white/5'
                  }`}
                >
                  {genre}
                </button>
              );
            })}
          </div>
        </div>

        {/* Movies Grid */}
        {loading ? (
          <div className="py-20 flex flex-col items-center justify-center space-y-3">
            <Loader2 className="w-10 h-10 text-[#E50914] animate-spin" />
            <p className="text-gray-400 text-sm">Retrieving real movie metadata from TMDB...</p>
          </div>
        ) : movies.length === 0 ? (
          <div className="py-20 text-center space-y-3">
            <p className="text-gray-300 font-semibold text-base">No movies found matching the selected filters.</p>
            <button
              onClick={() => {
                setSelectedLanguage('All');
                setSelectedGenre('All');
              }}
              className="text-xs bg-[#E50914] text-white px-4 py-2 rounded-md font-semibold"
            >
              Reset Filters
            </button>
          </div>
        ) : (
          <>
            <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-6 gap-3 sm:gap-4 pt-2">
              {movies.map(movie => (
                <MediaCard key={movie.id} item={movie} mediaType="movie" widthClass="w-full" />
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
                      <span>Load More Movies (Page {page + 1} of {totalPages})</span>
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
