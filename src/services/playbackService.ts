import { AuthorizedPlaybackSource } from '../types/tmdb';

const STORAGE_KEY = 'cineplus_authorized_playback_sources';

// Initial pre-configured authorized streams (e.g., licensed open-source test stream)
// Real titles without authorization (like Salaar) default to UNAVAILABLE
const INITIAL_SOURCES: AuthorizedPlaybackSource[] = [
  {
    tmdbId: 1376856, // Real title ID for licensed verification test
    mediaType: 'movie',
    title: 'The Paradise (Authorized Test Title)',
    availability: 'AVAILABLE',
    playbackType: 'progressive',
    playbackUrl: 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4',
    trailerUrl: 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4',
    audioLanguages: 'Telugu (Original), Hindi, English',
    subtitleLanguages: 'English, Telugu',
    availableQualities: '1080p Full HD, 720p HD, 480p SD',
    ottPlatform: 'CinePlus License',
    rightsNote: 'Authorized streaming source licensed for CinePlus verification.',
    updatedAt: Date.now()
  },
  {
    tmdbId: 770906, // Real TMDB ID for Salaar: Part 1 – Ceasefire
    mediaType: 'movie',
    title: 'Salaar: Part 1 – Ceasefire',
    availability: 'UNAVAILABLE', // Catalog only
    playbackType: 'progressive',
    playbackUrl: '', // No authorized full movie stream
    trailerUrl: 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4',
    audioLanguages: 'Telugu (Original), Hindi, Tamil, Kannada, Malayalam',
    subtitleLanguages: 'English, Telugu, Hindi',
    availableQualities: 'Theatrical Release',
    ottPlatform: 'Netflix / Theatrical',
    rightsNote: 'Available information only — streaming unavailable in this app. Theatrical release catalog metadata.',
    updatedAt: Date.now()
  }
];

export const playbackService = {
  getAllSources: (): AuthorizedPlaybackSource[] => {
    try {
      const stored = localStorage.getItem(STORAGE_KEY);
      if (!stored) {
        localStorage.setItem(STORAGE_KEY, JSON.stringify(INITIAL_SOURCES));
        return INITIAL_SOURCES;
      }
      return JSON.parse(stored);
    } catch {
      return INITIAL_SOURCES;
    }
  },

  getSourceByTmdbId: (tmdbId: number): AuthorizedPlaybackSource | null => {
    const sources = playbackService.getAllSources();
    return sources.find(s => s.tmdbId === tmdbId) || null;
  },

  isAuthorizedForStreaming: (tmdbId: number): boolean => {
    const source = playbackService.getSourceByTmdbId(tmdbId);
    return Boolean(source && source.availability === 'AVAILABLE' && source.playbackUrl && source.playbackUrl.trim().length > 0);
  },

  saveSource: (source: AuthorizedPlaybackSource): void => {
    const sources = playbackService.getAllSources();
    const index = sources.findIndex(s => s.tmdbId === source.tmdbId);
    let updated: AuthorizedPlaybackSource[];
    if (index >= 0) {
      updated = [...sources];
      updated[index] = { ...source, updatedAt: Date.now() };
    } else {
      updated = [...sources, { ...source, updatedAt: Date.now() }];
    }
    localStorage.setItem(STORAGE_KEY, JSON.stringify(updated));
  },

  deleteSource: (tmdbId: number): void => {
    const sources = playbackService.getAllSources();
    const updated = sources.filter(s => s.tmdbId !== tmdbId);
    localStorage.setItem(STORAGE_KEY, JSON.stringify(updated));
  },

  resetDefaults: (): void => {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(INITIAL_SOURCES));
  }
};
