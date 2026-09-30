import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { tmdbApi, tmdbImage } from '../services/tmdbApi';
import { playbackService } from '../services/playbackService';
import { Movie } from '../types/tmdb';
import {
  Play,
  Video,
  Star,
  Clock,
  Calendar,
  Globe,
  Plus,
  Check,
  Tv,
  AlertTriangle,
  Info,
  ArrowLeft,
  Loader2
} from 'lucide-react';

export const MovieDetailsPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [movie, setMovie] = useState<Movie | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isInList, setIsInList] = useState(false);
  const [showTrailerModal, setShowTrailerModal] = useState(false);

  useEffect(() => {
    const fetchMovie = async () => {
      if (!id) return;
      setLoading(true);
      setError(null);
      try {
        const data = await tmdbApi.getMovieDetails(Number(id));
        setMovie(data);
      } catch (err: any) {
        setError(err?.message || 'Failed to retrieve movie details from TMDB.');
      } finally {
        setLoading(false);
      }
    };

    fetchMovie();
    window.scrollTo(0, 0);
  }, [id]);

  if (loading) {
    return (
      <div className="min-h-screen bg-[#0B0F19] flex flex-col items-center justify-center space-y-3">
        <Loader2 className="w-12 h-12 text-[#E50914] animate-spin" />
        <p className="text-gray-400 text-sm">Retrieving real movie metadata from TMDB...</p>
      </div>
    );
  }

  if (error || !movie) {
    return (
      <div className="min-h-screen bg-[#0B0F19] flex flex-col items-center justify-center px-4 text-center space-y-4">
        <AlertTriangle className="w-12 h-12 text-[#E50914]" />
        <h2 className="text-xl font-bold text-white">Movie Metadata Not Available</h2>
        <p className="text-gray-400 text-sm max-w-md">{error || 'Could not locate this title in TMDB.'}</p>
        <button
          onClick={() => navigate(-1)}
          className="bg-[#141A29] hover:bg-[#1E273D] text-white px-5 py-2.5 rounded-md font-semibold text-sm"
        >
          Go Back
        </button>
      </div>
    );
  }

  const isPlayable = playbackService.isAuthorizedForStreaming(movie.id);
  const authorizedSource = playbackService.getSourceByTmdbId(movie.id);

  // Watch providers for India (IN)
  const indiaWatchData = movie['watch/providers']?.results?.['IN'];

  // Cast & Crew
  const director = movie.credits?.crew.find(c => c.job === 'Director')?.name || 'Not Listed';
  const topCast = movie.credits?.cast.slice(0, 8) || [];

  // Trailer
  const trailer = movie.videos?.results.find(v => v.type === 'Trailer' && v.site === 'YouTube') || movie.videos?.results[0];

  const releaseYear = movie.release_date ? movie.release_date.split('-')[0] : '2024';
  const hours = movie.runtime ? Math.floor(movie.runtime / 60) : 2;
  const minutes = movie.runtime ? movie.runtime % 60 : 15;

  return (
    <div className="min-h-screen bg-[#0B0F19] pb-24 text-white">
      {/* Backdrop Header with Scrim */}
      <div className="relative w-full h-[55vh] md:h-[70vh] overflow-hidden">
        <img
          src={tmdbImage.backdrop(movie.backdrop_path, 'original')}
          alt={movie.title}
          className="w-full h-full object-cover object-top"
        />
        <div className="absolute inset-0 bg-gradient-to-t from-[#0B0F19] via-[#0B0F19]/70 to-transparent" />
        <div className="absolute inset-0 bg-gradient-to-r from-[#0B0F19] via-[#0B0F19]/50 to-transparent hidden md:block" />

        {/* Back Button */}
        <button
          onClick={() => navigate(-1)}
          className="absolute top-20 left-4 sm:left-8 z-30 p-2.5 rounded-full bg-black/60 hover:bg-[#E50914] text-white transition-colors backdrop-blur-sm"
          aria-label="Back"
        >
          <ArrowLeft className="w-5 h-5" />
        </button>
      </div>

      {/* Main Details Body */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 -mt-36 sm:-mt-48 relative z-20 space-y-10">
        <div className="flex flex-col md:flex-row gap-8 items-start">
          {/* Poster Column */}
          <div className="w-44 sm:w-56 md:w-64 flex-shrink-0 mx-auto md:mx-0">
            <div className="rounded-xl overflow-hidden shadow-2xl border-2 border-white/10 bg-[#141A29]">
              <img
                src={tmdbImage.poster(movie.poster_path, 'w500')}
                alt={movie.title}
                className="w-full h-auto object-cover"
              />
            </div>
          </div>

          {/* Metadata Column */}
          <div className="flex-1 space-y-5">
            <div>
              <div className="flex flex-wrap items-center gap-2 text-xs mb-2">
                <span className="bg-[#E50914] text-white font-bold px-2 py-0.5 rounded uppercase text-[10px]">
                  TMDB ID: {movie.id}
                </span>
                <span className="text-gray-400 font-semibold">{releaseYear}</span>
                <span>•</span>
                <span className="text-gray-400">{hours}h {minutes}m</span>
                <span>•</span>
                <span className="text-[#00E5FF] font-semibold uppercase">{movie.original_language?.toUpperCase() || 'EN'}</span>
                <span>•</span>
                <div className="flex items-center text-[#FFD700] space-x-1 font-bold">
                  <Star className="w-3.5 h-3.5 fill-[#FFD700]" />
                  <span>{movie.vote_average?.toFixed(1) || '7.5'}</span>
                  <span className="text-gray-400 font-normal">({movie.vote_count} votes)</span>
                </div>
              </div>

              <h1 className="text-3xl sm:text-4xl lg:text-5xl font-black text-white tracking-tight leading-tight">
                {movie.title}
              </h1>

              {movie.original_title && movie.original_title !== movie.title && (
                <p className="text-sm text-[#00E5FF] font-medium mt-1">
                  Original Title: {movie.original_title}
                </p>
              )}
            </div>

            {/* Genres */}
            {movie.genres && movie.genres.length > 0 && (
              <div className="flex flex-wrap gap-2">
                {movie.genres.map(g => (
                  <span
                    key={g.id}
                    className="text-xs bg-[#141A29] border border-white/10 text-gray-300 px-3 py-1 rounded-full font-medium"
                  >
                    {g.name}
                  </span>
                ))}
              </div>
            )}

            {/* Playback Authorization Banner */}
            <div className={`p-4 rounded-xl border ${
              isPlayable
                ? 'bg-emerald-950/40 border-emerald-500/40 text-emerald-300'
                : 'bg-amber-950/40 border-amber-500/30 text-amber-200'
            }`}>
              <div className="flex items-start space-x-3">
                {isPlayable ? (
                  <Check className="w-5 h-5 text-emerald-400 mt-0.5 flex-shrink-0" />
                ) : (
                  <AlertTriangle className="w-5 h-5 text-amber-400 mt-0.5 flex-shrink-0" />
                )}
                <div className="space-y-1 text-xs sm:text-sm">
                  <p className="font-bold">
                    {isPlayable
                      ? 'AUTHORIZED IN-APP STREAMING SOURCE AVAILABLE'
                      : 'Available information only — streaming unavailable in this app.'}
                  </p>
                  <p className="text-gray-300 text-xs leading-relaxed">
                    {isPlayable
                      ? `This title is verified for playback through ${authorizedSource?.ottPlatform || 'licensed source'}.`
                      : 'Streaming source is not available. Commercial distribution rights are held by external distributors. You can watch the official trailer or see legal watch providers below.'}
                  </p>
                </div>
              </div>
            </div>

            {/* Action Buttons */}
            <div className="flex flex-wrap items-center gap-3 pt-1">
              {isPlayable ? (
                <button
                  onClick={() => navigate(`/watch/${movie.id}?type=movie`)}
                  className="flex items-center space-x-2 bg-[#E50914] hover:bg-[#F40612] text-white font-black px-8 py-3.5 rounded-lg transition-transform hover:scale-105 shadow-xl shadow-red-600/30 text-sm"
                >
                  <Play className="w-5 h-5 fill-white" />
                  <span>PLAY MOVIE</span>
                </button>
              ) : (
                <div className="bg-[#141A29] text-gray-400 border border-white/10 px-5 py-3 rounded-lg text-xs font-semibold">
                  Streaming source is not available
                </div>
              )}

              {trailer && (
                <button
                  onClick={() => setShowTrailerModal(true)}
                  className="flex items-center space-x-2 bg-white/10 hover:bg-white/20 text-white font-semibold px-6 py-3.5 rounded-lg transition-colors border border-white/10 text-sm"
                >
                  <Video className="w-5 h-5 text-amber-400" />
                  <span>Watch Trailer</span>
                </button>
              )}

              <button
                onClick={() => setIsInList(!isInList)}
                className="flex items-center space-x-2 bg-[#141A29] hover:bg-[#1E273D] border border-white/15 text-white font-semibold px-5 py-3.5 rounded-lg transition-colors text-sm"
              >
                {isInList ? <Check className="w-4 h-4 text-emerald-400" /> : <Plus className="w-4 h-4" />}
                <span>{isInList ? 'Added to List' : 'Add to My List'}</span>
              </button>
            </div>

            {/* Synopsis */}
            <div className="space-y-2 pt-2">
              <h3 className="text-base font-bold text-white uppercase tracking-wider text-xs text-gray-400">
                Synopsis
              </h3>
              <p className="text-gray-300 text-sm sm:text-base leading-relaxed">
                {movie.overview || 'No synopsis provided by the metadata provider.'}
              </p>
            </div>

            {/* Production & Crew Details */}
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 pt-2 text-xs border-t border-white/10 text-gray-400">
              <div>
                <span className="font-semibold text-gray-300">Director: </span>
                <span className="text-white">{director}</span>
              </div>
              <div>
                <span className="font-semibold text-gray-300">Release Date: </span>
                <span className="text-white">{movie.release_date || 'Unknown'}</span>
              </div>
              {movie.production_companies && movie.production_companies.length > 0 && (
                <div className="sm:col-span-2">
                  <span className="font-semibold text-gray-300">Production Companies: </span>
                  <span className="text-gray-200">
                    {movie.production_companies.map(p => p.name).join(', ')}
                  </span>
                </div>
              )}
            </div>
          </div>
        </div>

        {/* Regional Watch Availability Section (India Primary Region) */}
        <div className="bg-[#141A29]/80 border border-white/10 rounded-2xl p-6 space-y-4">
          <div className="flex items-center space-x-2 text-white">
            <Tv className="w-5 h-5 text-[#00E5FF]" />
            <h2 className="text-lg font-bold">Where to Watch Legally (India Region)</h2>
          </div>

          {indiaWatchData ? (
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 pt-2">
              {/* Flatrate Subscription */}
              <div className="space-y-2">
                <span className="text-xs font-bold text-emerald-400 uppercase tracking-wider">
                  📺 Streaming / Subscription
                </span>
                {indiaWatchData.flatrate && indiaWatchData.flatrate.length > 0 ? (
                  <div className="flex flex-wrap gap-2">
                    {indiaWatchData.flatrate.map(p => (
                      <div key={p.provider_id} className="flex items-center space-x-2 bg-[#0B0F19] px-3 py-1.5 rounded-lg border border-white/10">
                        {p.logo_path && (
                          <img src={tmdbImage.thumbnail(p.logo_path)} alt={p.provider_name} className="w-5 h-5 rounded" />
                        )}
                        <span className="text-xs font-medium text-white">{p.provider_name}</span>
                      </div>
                    ))}
                  </div>
                ) : (
                  <p className="text-xs text-gray-400">Not currently on SVOD subscription in India.</p>
                )}
              </div>

              {/* Rent */}
              <div className="space-y-2">
                <span className="text-xs font-bold text-[#00E5FF] uppercase tracking-wider">
                  🎟️ Rent
                </span>
                {indiaWatchData.rent && indiaWatchData.rent.length > 0 ? (
                  <div className="flex flex-wrap gap-2">
                    {indiaWatchData.rent.map(p => (
                      <div key={p.provider_id} className="flex items-center space-x-2 bg-[#0B0F19] px-3 py-1.5 rounded-lg border border-white/10">
                        {p.logo_path && (
                          <img src={tmdbImage.thumbnail(p.logo_path)} alt={p.provider_name} className="w-5 h-5 rounded" />
                        )}
                        <span className="text-xs font-medium text-white">{p.provider_name}</span>
                      </div>
                    ))}
                  </div>
                ) : (
                  <p className="text-xs text-gray-400">Rental options not listed.</p>
                )}
              </div>

              {/* Buy */}
              <div className="space-y-2">
                <span className="text-xs font-bold text-amber-400 uppercase tracking-wider">
                  💳 Buy
                </span>
                {indiaWatchData.buy && indiaWatchData.buy.length > 0 ? (
                  <div className="flex flex-wrap gap-2">
                    {indiaWatchData.buy.map(p => (
                      <div key={p.provider_id} className="flex items-center space-x-2 bg-[#0B0F19] px-3 py-1.5 rounded-lg border border-white/10">
                        {p.logo_path && (
                          <img src={tmdbImage.thumbnail(p.logo_path)} alt={p.provider_name} className="w-5 h-5 rounded" />
                        )}
                        <span className="text-xs font-medium text-white">{p.provider_name}</span>
                      </div>
                    ))}
                  </div>
                ) : (
                  <p className="text-xs text-gray-400">Purchase options not listed.</p>
                )}
              </div>
            </div>
          ) : (
            <p className="text-xs text-gray-400 leading-relaxed">
              No regional digital OTT streaming provider information found for India at this time. This title may currently be in theatrical release or broadcast syndication.
            </p>
          )}
        </div>

        {/* Top Star Cast */}
        {topCast.length > 0 && (
          <div className="space-y-4">
            <h2 className="text-lg font-bold text-white tracking-wide">Top Star Cast</h2>
            <div className="grid grid-cols-2 sm:grid-cols-4 md:grid-cols-6 lg:grid-cols-8 gap-3">
              {topCast.map(actor => (
                <div key={actor.id} className="bg-[#141A29] rounded-lg p-2.5 text-center space-y-1.5 border border-white/5">
                  <div className="w-16 h-16 mx-auto rounded-full overflow-hidden bg-gray-800 border border-white/10">
                    <img
                      src={tmdbImage.profile(actor.profile_path)}
                      alt={actor.name}
                      className="w-full h-full object-cover"
                      loading="lazy"
                    />
                  </div>
                  <h4 className="text-xs font-bold text-white truncate">{actor.name}</h4>
                  <p className="text-[10px] text-gray-400 truncate">{actor.character}</p>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>

      {/* Trailer Modal */}
      {showTrailerModal && trailer && (
        <div className="fixed inset-0 z-50 bg-black/90 flex items-center justify-center p-4">
          <div className="relative w-full max-w-4xl aspect-video bg-black rounded-2xl overflow-hidden shadow-2xl border border-white/15">
            <button
              onClick={() => setShowTrailerModal(false)}
              className="absolute top-3 right-3 z-10 bg-white/20 hover:bg-white/40 text-white rounded-full p-2"
            >
              ✕
            </button>
            <iframe
              src={tmdbImage.youtubeEmbed(trailer.key)}
              title={`${movie.title} Official Trailer`}
              className="w-full h-full border-0"
              allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
              allowFullScreen
            />
          </div>
        </div>
      )}
    </div>
  );
};
