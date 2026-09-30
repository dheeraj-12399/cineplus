import {
  Movie,
  TvShow,
  SeasonDetails,
  MultiSearchResult,
  TmdbPageResponse
} from '../types/tmdb';

const BASE_URL = 'https://api.themoviedb.org/3';
const IMAGE_BASE_URL = 'https://image.tmdb.org/t/p';

// Read API key from environment variable (Vite prefix) or fallback to public educational key
const FALLBACK_KEY = '4e44d9029b1270a757cddc766a1bcb63';

export const getTmdbApiKey = (): string => {
  const envKey = import.meta.env.VITE_TMDB_API_KEY;
  if (envKey && typeof envKey === 'string' && envKey.trim() !== '' && !envKey.includes('your_tmdb_api_key')) {
    return envKey.trim();
  }
  return FALLBACK_KEY;
};

async function fetchFromTmdb<T>(endpoint: string, params: Record<string, string | number | boolean | undefined> = {}): Promise<T> {
  const apiKey = getTmdbApiKey();
  const url = new URL(`${BASE_URL}/${endpoint}`);
  url.searchParams.append('api_key', apiKey);

  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') {
      url.searchParams.append(key, String(value));
    }
  });

  const response = await fetch(url.toString(), {
    headers: {
      Accept: 'application/json'
    }
  });

  if (!response.ok) {
    const errorText = await response.text().catch(() => '');
    throw new Error(`TMDB API request failed (${response.status}): ${errorText || response.statusText}`);
  }

  return response.json();
}

// --- Official Image URL Helpers ---
export const tmdbImage = {
  poster: (path: string | null | undefined, size: 'w342' | 'w500' | 'w780' | 'original' = 'w500'): string => {
    if (!path) return 'https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=500&auto=format&fit=crop&q=80';
    return `${IMAGE_BASE_URL}/${size}${path}`;
  },
  backdrop: (path: string | null | undefined, size: 'w780' | 'w1280' | 'original' = 'w1280'): string => {
    if (!path) return 'https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=1280&auto=format&fit=crop&q=80';
    return `${IMAGE_BASE_URL}/${size}${path}`;
  },
  profile: (path: string | null | undefined, size: 'w185' | 'h632' | 'original' = 'w185'): string => {
    if (!path) return 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=185&auto=format&fit=crop&q=80';
    return `${IMAGE_BASE_URL}/${size}${path}`;
  },
  thumbnail: (path: string | null | undefined): string => {
    if (!path) return 'https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=342&auto=format&fit=crop&q=80';
    return `${IMAGE_BASE_URL}/w342${path}`;
  },
  youtubeTrailer: (key: string | null | undefined): string => {
    if (!key) return '';
    return `https://www.youtube.com/watch?v=${key}`;
  },
  youtubeEmbed: (key: string | null | undefined): string => {
    if (!key) return '';
    return `https://www.youtube.com/embed/${key}?autoplay=1`;
  }
};

// --- Language Code Mapping ---
export const LANGUAGE_MAP: Record<string, { code: string; label: string; flag: string }> = {
  All: { code: '', label: 'All Languages', flag: '🌐' },
  Telugu: { code: 'te', label: 'Telugu Cinema', flag: '🇮🇳' },
  Hindi: { code: 'hi', label: 'Hindi Bollywood', flag: '🇮🇳' },
  Tamil: { code: 'ta', label: 'Tamil Cinema', flag: '🇮🇳' },
  Malayalam: { code: 'ml', label: 'Malayalam Cinema', flag: '🇮🇳' },
  Kannada: { code: 'kn', label: 'Kannada Cinema', flag: '🇮🇳' },
  English: { code: 'en', label: 'English Hits', flag: '🎬' },
  Bengali: { code: 'bn', label: 'Bengali Cinema', flag: '🇮🇳' },
  Marathi: { code: 'mr', label: 'Marathi Cinema', flag: '🇮🇳' },
  Punjabi: { code: 'pa', label: 'Punjabi Cinema', flag: '🇮🇳' }
};

export const GENRE_MAP: Record<string, number> = {
  Action: 28,
  Adventure: 12,
  Animation: 16,
  Comedy: 35,
  Crime: 80,
  Documentary: 99,
  Drama: 18,
  Family: 10751,
  Fantasy: 14,
  History: 36,
  Horror: 27,
  Music: 10402,
  Mystery: 9648,
  Romance: 10749,
  'Sci-Fi': 878,
  Thriller: 53,
  War: 10752,
  Western: 37
};

// --- API Service Methods ---
export const tmdbApi = {
  // Movies Discovery
  getTrendingMovies: (page = 1) =>
    fetchFromTmdb<TmdbPageResponse<Movie>>('trending/movie/week', { page }),

  getPopularMovies: (page = 1) =>
    fetchFromTmdb<TmdbPageResponse<Movie>>('movie/popular', { page }),

  getNowPlayingMovies: (page = 1) =>
    fetchFromTmdb<TmdbPageResponse<Movie>>('movie/now_playing', { page }),

  getUpcomingMovies: (page = 1) =>
    fetchFromTmdb<TmdbPageResponse<Movie>>('movie/upcoming', { page }),

  getTopRatedMovies: (page = 1) =>
    fetchFromTmdb<TmdbPageResponse<Movie>>('movie/top_rated', { page }),

  discoverMovies: (params: {
    language?: string;
    genreId?: number;
    sortBy?: string;
    year?: number;
    page?: number;
  }) =>
    fetchFromTmdb<TmdbPageResponse<Movie>>('discover/movie', {
      with_original_language: params.language,
      with_genres: params.genreId,
      sort_by: params.sortBy || 'popularity.desc',
      primary_release_year: params.year,
      page: params.page || 1
    }),

  // TV Discovery
  getTrendingTv: (page = 1) =>
    fetchFromTmdb<TmdbPageResponse<TvShow>>('trending/tv/week', { page }),

  getPopularTv: (page = 1) =>
    fetchFromTmdb<TmdbPageResponse<TvShow>>('tv/popular', { page }),

  getAiringTodayTv: (page = 1) =>
    fetchFromTmdb<TmdbPageResponse<TvShow>>('tv/airing_today', { page }),

  getOnTheAirTv: (page = 1) =>
    fetchFromTmdb<TmdbPageResponse<TvShow>>('tv/on_the_air', { page }),

  getTopRatedTv: (page = 1) =>
    fetchFromTmdb<TmdbPageResponse<TvShow>>('tv/top_rated', { page }),

  discoverTv: (params: {
    language?: string;
    genreId?: number;
    sortBy?: string;
    page?: number;
  }) =>
    fetchFromTmdb<TmdbPageResponse<TvShow>>('discover/tv', {
      with_original_language: params.language,
      with_genres: params.genreId,
      sort_by: params.sortBy || 'popularity.desc',
      page: params.page || 1
    }),

  // Details
  getMovieDetails: (id: number) =>
    fetchFromTmdb<Movie>(`movie/${id}`, {
      append_to_response: 'credits,videos,watch/providers'
    }),

  getTvDetails: (id: number) =>
    fetchFromTmdb<TvShow>(`tv/${id}`, {
      append_to_response: 'credits,videos,watch/providers'
    }),

  getTvSeasonDetails: (tvId: number, seasonNumber: number) =>
    fetchFromTmdb<SeasonDetails>(`tv/${tvId}/season/${seasonNumber}`),

  // Search
  searchMulti: (query: string, page = 1) =>
    fetchFromTmdb<TmdbPageResponse<MultiSearchResult>>('search/multi', {
      query,
      page,
      include_adult: false
    }),

  searchMovies: (query: string, page = 1) =>
    fetchFromTmdb<TmdbPageResponse<Movie>>('search/movie', {
      query,
      page,
      include_adult: false
    }),

  searchTv: (query: string, page = 1) =>
    fetchFromTmdb<TmdbPageResponse<TvShow>>('search/tv', {
      query,
      page,
      include_adult: false
    })
};
