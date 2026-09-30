import React, { useState, useEffect } from 'react';
import { tmdbApi } from '../services/tmdbApi';
import { Movie, TvShow } from '../types/tmdb';
import { HeroBanner } from '../components/HeroBanner';
import { MovieRow } from '../components/MovieRow';
import { AlertTriangle, RefreshCw } from 'lucide-react';

export const HomePage: React.FC = () => {
  const [featuredMovie, setFeaturedMovie] = useState<Movie | null>(null);
  const [trendingMovies, setTrendingMovies] = useState<Movie[]>([]);
  const [popularMovies, setPopularMovies] = useState<Movie[]>([]);
  const [nowPlayingMovies, setNowPlayingMovies] = useState<Movie[]>([]);
  const [topRatedMovies, setTopRatedMovies] = useState<Movie[]>([]);

  // Regional Indian Cinema
  const [teluguMovies, setTeluguMovies] = useState<Movie[]>([]);
  const [hindiMovies, setHindiMovies] = useState<Movie[]>([]);
  const [tamilMovies, setTamilMovies] = useState<Movie[]>([]);
  const [malayalamMovies, setMalayalamMovies] = useState<Movie[]>([]);
  const [kannadaMovies, setKannadaMovies] = useState<Movie[]>([]);
  const [englishMovies, setEnglishMovies] = useState<Movie[]>([]);
  const [bengaliMovies, setBengaliMovies] = useState<Movie[]>([]);
  const [marathiMovies, setMarathiMovies] = useState<Movie[]>([]);
  const [punjabiMovies, setPunjabiMovies] = useState<Movie[]>([]);

  // TV Shows
  const [trendingTv, setTrendingTv] = useState<TvShow[]>([]);
  const [popularTv, setPopularTv] = useState<TvShow[]>([]);
  const [topRatedTv, setTopRatedTv] = useState<TvShow[]>([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const loadData = async () => {
    setLoading(true);
    setError(null);
    try {
      const [
        trendingRes,
        popularRes,
        nowPlayingRes,
        topRatedRes,
        teluguRes,
        hindiRes,
        tamilRes,
        malayalamRes,
        kannadaRes,
        englishRes,
        bengaliRes,
        marathiRes,
        punjabiRes,
        trendingTvRes,
        popularTvRes,
        topRatedTvRes
      ] = await Promise.allSettled([
        tmdbApi.getTrendingMovies(1),
        tmdbApi.getPopularMovies(1),
        tmdbApi.getNowPlayingMovies(1),
        tmdbApi.getTopRatedMovies(1),
        tmdbApi.discoverMovies({ language: 'te', sortBy: 'popularity.desc' }),
        tmdbApi.discoverMovies({ language: 'hi', sortBy: 'popularity.desc' }),
        tmdbApi.discoverMovies({ language: 'ta', sortBy: 'popularity.desc' }),
        tmdbApi.discoverMovies({ language: 'ml', sortBy: 'popularity.desc' }),
        tmdbApi.discoverMovies({ language: 'kn', sortBy: 'popularity.desc' }),
        tmdbApi.discoverMovies({ language: 'en', sortBy: 'popularity.desc' }),
        tmdbApi.discoverMovies({ language: 'bn', sortBy: 'popularity.desc' }),
        tmdbApi.discoverMovies({ language: 'mr', sortBy: 'popularity.desc' }),
        tmdbApi.discoverMovies({ language: 'pa', sortBy: 'popularity.desc' }),
        tmdbApi.getTrendingTv(1),
        tmdbApi.getPopularTv(1),
        tmdbApi.getTopRatedTv(1)
      ]);

      const trending = trendingRes.status === 'fulfilled' ? trendingRes.value.results : [];
      const popular = popularRes.status === 'fulfilled' ? popularRes.value.results : [];
      const nowPlaying = nowPlayingRes.status === 'fulfilled' ? nowPlayingRes.value.results : [];
      const topRated = topRatedRes.status === 'fulfilled' ? topRatedRes.value.results : [];

      const telugu = teluguRes.status === 'fulfilled' ? teluguRes.value.results : [];
      const hindi = hindiRes.status === 'fulfilled' ? hindiRes.value.results : [];
      const tamil = tamilRes.status === 'fulfilled' ? tamilRes.value.results : [];
      const malayalam = malayalamRes.status === 'fulfilled' ? malayalamRes.value.results : [];
      const kannada = kannadaRes.status === 'fulfilled' ? kannadaRes.value.results : [];
      const english = englishRes.status === 'fulfilled' ? englishRes.value.results : [];
      const bengali = bengaliRes.status === 'fulfilled' ? bengaliRes.value.results : [];
      const marathi = marathiRes.status === 'fulfilled' ? marathiRes.value.results : [];
      const punjabi = punjabiRes.status === 'fulfilled' ? punjabiRes.value.results : [];

      const trendTv = trendingTvRes.status === 'fulfilled' ? trendingTvRes.value.results : [];
      const popTv = popularTvRes.status === 'fulfilled' ? popularTvRes.value.results : [];
      const topTv = topRatedTvRes.status === 'fulfilled' ? topRatedTvRes.value.results : [];

      setTrendingMovies(trending);
      setPopularMovies(popular);
      setNowPlayingMovies(nowPlaying);
      setTopRatedMovies(topRated);

      setTeluguMovies(telugu);
      setHindiMovies(hindi);
      setTamilMovies(tamil);
      setMalayalamMovies(malayalam);
      setKannadaMovies(kannada);
      setEnglishMovies(english);
      setBengaliMovies(bengali);
      setMarathiMovies(marathi);
      setPunjabiMovies(punjabi);

      setTrendingTv(trendTv);
      setPopularTv(popTv);
      setTopRatedTv(topTv);

      // Prioritize prominent title for hero (e.g. Salaar if found, or first trending)
      const salaar = telugu.find(m => m.title.toLowerCase().includes('salaar')) || trending[0] || popular[0] || null;
      setFeaturedMovie(salaar);
    } catch (err: any) {
      setError(err?.message || 'Failed to load movie catalog from TMDB.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  if (loading) {
    return (
      <div className="min-h-screen bg-[#0B0F19] flex flex-col items-center justify-center space-y-4">
        <div className="w-12 h-12 border-4 border-[#E50914] border-t-transparent rounded-full animate-spin" />
        <p className="text-gray-400 text-sm font-medium">Connecting to official TMDB API...</p>
      </div>
    );
  }

  if (error && trendingMovies.length === 0) {
    return (
      <div className="min-h-screen bg-[#0B0F19] flex flex-col items-center justify-center px-4 text-center space-y-4">
        <AlertTriangle className="w-12 h-12 text-[#E50914]" />
        <h2 className="text-xl font-bold text-white">Catalog Connection Issue</h2>
        <p className="text-gray-400 text-sm max-w-md">{error}</p>
        <button
          onClick={loadData}
          className="flex items-center space-x-2 bg-[#E50914] hover:bg-[#F40612] text-white px-5 py-2.5 rounded-md font-semibold text-sm transition-all"
        >
          <RefreshCw className="w-4 h-4" />
          <span>Retry Connection</span>
        </button>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-[#0B0F19] pb-16">
      {/* Cinematic Hero Spotlight */}
      <HeroBanner movie={featuredMovie} />

      {/* Real Movie & TV Rows */}
      <div className="space-y-4 -mt-10 sm:-mt-14 relative z-20">
        {/* 1. Telugu Cinema Spotlight */}
        <MovieRow
          title="Telugu Cinema"
          items={teluguMovies}
          seeAllLink="/movies?lang=Telugu"
          badge="Tollywood"
          icon="🎬"
        />

        {/* 2. Trending Movies Worldwide */}
        <MovieRow
          title="Trending Movies Worldwide"
          items={trendingMovies}
          seeAllLink="/movies"
          badge="Live Hits"
          icon="🔥"
        />

        {/* 3. Hindi Cinema */}
        <MovieRow
          title="Hindi Bollywood"
          items={hindiMovies}
          seeAllLink="/movies?lang=Hindi"
          badge="Bollywood"
          icon="⭐"
        />

        {/* 4. Tamil Cinema */}
        <MovieRow
          title="Tamil Cinema"
          items={tamilMovies}
          seeAllLink="/movies?lang=Tamil"
          badge="Kollywood"
          icon="🎭"
        />

        {/* 5. Malayalam Cinema */}
        <MovieRow
          title="Malayalam Cinema"
          items={malayalamMovies}
          seeAllLink="/movies?lang=Malayalam"
          badge="Mollywood"
          icon="🌟"
        />

        {/* 6. Kannada Cinema */}
        <MovieRow
          title="Kannada Cinema"
          items={kannadaMovies}
          seeAllLink="/movies?lang=Kannada"
          badge="Sandalwood"
          icon="🌴"
        />

        {/* 7. English Hollywood Hits */}
        <MovieRow
          title="English Cinema"
          items={englishMovies}
          seeAllLink="/movies?lang=English"
          badge="Global"
          icon="🌐"
        />

        {/* 8. Regional India: Bengali, Marathi, Punjabi */}
        {bengaliMovies.length > 0 && (
          <MovieRow
            title="Bengali Cinema"
            items={bengaliMovies}
            seeAllLink="/movies?lang=Bengali"
            badge="Tollywood East"
            icon="🇮🇳"
          />
        )}

        {marathiMovies.length > 0 && (
          <MovieRow
            title="Marathi Cinema"
            items={marathiMovies}
            seeAllLink="/movies?lang=Marathi"
            badge="Regional"
            icon="🇮🇳"
          />
        )}

        {punjabiMovies.length > 0 && (
          <MovieRow
            title="Punjabi Cinema"
            items={punjabiMovies}
            seeAllLink="/movies?lang=Punjabi"
            badge="Pollywood"
            icon="🇮🇳"
          />
        )}

        {/* 9. Popular TV Shows */}
        <MovieRow
          title="Trending TV Series"
          items={trendingTv}
          mediaType="tv"
          seeAllLink="/tv"
          badge="Series"
          icon="📺"
        />

        {/* 10. Top Rated Movies */}
        <MovieRow
          title="Top Rated All-Time"
          items={topRatedMovies}
          seeAllLink="/movies"
          badge="Critically Acclaimed"
          icon="🏆"
        />
      </div>
    </div>
  );
};
