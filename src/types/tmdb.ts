export interface Movie {
  id: number;
  title: string;
  original_title: string;
  overview: string;
  poster_path: string | null;
  backdrop_path: string | null;
  release_date: string;
  vote_average: number;
  vote_count: number;
  popularity: number;
  genre_ids?: number[];
  genres?: { id: number; name: string }[];
  runtime?: number;
  original_language?: string;
  production_companies?: { id: number; name: string; logo_path: string | null }[];
  credits?: {
    cast: CastMember[];
    crew: CrewMember[];
  };
  videos?: {
    results: VideoItem[];
  };
  'watch/providers'?: {
    results: Record<string, WatchProviderRegion>;
  };
}

export interface TvShow {
  id: number;
  name: string;
  original_name: string;
  overview: string;
  poster_path: string | null;
  backdrop_path: string | null;
  first_air_date: string;
  vote_average: number;
  vote_count: number;
  popularity: number;
  genre_ids?: number[];
  genres?: { id: number; name: string }[];
  number_of_seasons?: number;
  number_of_episodes?: number;
  seasons?: SeasonSummary[];
  created_by?: { id: number; name: string; profile_path: string | null }[];
  original_language?: string;
  credits?: {
    cast: CastMember[];
    crew: CrewMember[];
  };
  videos?: {
    results: VideoItem[];
  };
  'watch/providers'?: {
    results: Record<string, WatchProviderRegion>;
  };
}

export interface SeasonSummary {
  id: number;
  season_number: number;
  name: string;
  overview: string;
  poster_path: string | null;
  episode_count: number;
  air_date: string;
}

export interface SeasonDetails {
  id: number;
  season_number: number;
  name: string;
  overview: string;
  poster_path: string | null;
  episodes: Episode[];
}

export interface Episode {
  id: number;
  episode_number: number;
  season_number: number;
  name: string;
  overview: string;
  still_path: string | null;
  runtime?: number;
  vote_average: number;
  air_date: string;
}

export interface CastMember {
  id: number;
  name: string;
  character: string;
  profile_path: string | null;
  order: number;
}

export interface CrewMember {
  id: number;
  name: string;
  job: string;
  department: string;
  profile_path: string | null;
}

export interface VideoItem {
  id: string;
  key: string;
  name: string;
  site: string;
  type: string;
  official: boolean;
}

export interface WatchProviderItem {
  provider_id: number;
  provider_name: string;
  logo_path: string;
}

export interface WatchProviderRegion {
  link?: string;
  flatrate?: WatchProviderItem[];
  rent?: WatchProviderItem[];
  buy?: WatchProviderItem[];
  ads?: WatchProviderItem[];
}

export interface MultiSearchResult {
  id: number;
  media_type: 'movie' | 'tv' | 'person';
  title?: string;
  name?: string;
  original_title?: string;
  original_name?: string;
  overview?: string;
  poster_path?: string | null;
  backdrop_path?: string | null;
  profile_path?: string | null;
  release_date?: string;
  first_air_date?: string;
  vote_average?: number;
  known_for_department?: string;
  known_for?: (Movie | TvShow)[];
}

export interface TmdbPageResponse<T> {
  page: number;
  results: T[];
  total_pages: number;
  total_results: number;
}

export interface AuthorizedPlaybackSource {
  tmdbId: number;
  mediaType: 'movie' | 'tv';
  title: string;
  availability: 'AVAILABLE' | 'UNAVAILABLE' | 'COMING_SOON';
  playbackType: 'progressive' | 'hls' | 'dash';
  playbackUrl: string;
  trailerUrl?: string;
  audioLanguages?: string;
  subtitleLanguages?: string;
  availableQualities?: string;
  ottPlatform?: string;
  rightsNote?: string;
  updatedAt: number;
}
