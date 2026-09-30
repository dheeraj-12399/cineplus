import React, { useState, useEffect } from 'react';
import { useParams, useSearchParams, useNavigate } from 'react-router-dom';
import { tmdbApi } from '../services/tmdbApi';
import { playbackService } from '../services/playbackService';
import { Movie, TvShow, AuthorizedPlaybackSource } from '../types/tmdb';
import { ArrowLeft, AlertTriangle, ShieldAlert, Film, Tv, Play, Info } from 'lucide-react';

export const WatchPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const [searchParams] = useSearchParams();
  const mediaType = (searchParams.get('type') as 'movie' | 'tv') || 'movie';
  const navigate = useNavigate();

  const [mediaTitle, setMediaTitle] = useState<string>('Streaming Title');
  const [authorizedSource, setAuthorizedSource] = useState<AuthorizedPlaybackSource | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const checkPlaybackRights = async () => {
      if (!id) return;
      setLoading(true);
      const tmdbIdNum = Number(id);

      // Check whether this title has an authorized playback source in the service
      const source = playbackService.getSourceByTmdbId(tmdbIdNum);
      setAuthorizedSource(source);

      // Fetch official title from TMDB
      try {
        if (mediaType === 'tv') {
          const show = await tmdbApi.getTvDetails(tmdbIdNum);
          setMediaTitle(show.name);
        } else {
          const movie = await tmdbApi.getMovieDetails(tmdbIdNum);
          setMediaTitle(movie.title);
        }
      } catch {
        if (source) setMediaTitle(source.title);
      } finally {
        setLoading(false);
      }
    };

    checkPlaybackRights();
  }, [id, mediaType]);

  const isPlayable = Boolean(authorizedSource && authorizedSource.availability === 'AVAILABLE' && authorizedSource.playbackUrl && authorizedSource.playbackUrl.trim().length > 0);

  if (loading) {
    return (
      <div className="min-h-screen bg-black flex flex-col items-center justify-center text-white">
        <div className="w-12 h-12 border-4 border-[#E50914] border-t-transparent rounded-full animate-spin mb-4" />
        <p className="text-gray-400 text-sm">Verifying authorized playback stream...</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-black text-white flex flex-col justify-between">
      {/* Top Navigation Bar */}
      <div className="fixed top-0 left-0 right-0 z-40 bg-gradient-to-b from-black/90 to-transparent p-4 sm:p-6 flex items-center justify-between">
        <button
          onClick={() => navigate(-1)}
          className="flex items-center space-x-2 bg-white/10 hover:bg-white/20 text-white px-4 py-2 rounded-full text-xs font-semibold backdrop-blur-md transition-colors"
        >
          <ArrowLeft className="w-4 h-4" />
          <span>Exit Player</span>
        </button>

        <div className="text-center">
          <h1 className="text-sm sm:text-base font-bold text-white drop-shadow truncate max-w-xs sm:max-w-md">
            {mediaTitle}
          </h1>
          <span className="text-[10px] text-gray-400 uppercase tracking-widest">
            {isPlayable ? 'Authorized Player' : 'Streaming Rights Notice'}
          </span>
        </div>

        <div className="w-20" />
      </div>

      {/* Main Playback or Unauthorized Notice Container */}
      <div className="flex-1 flex items-center justify-center p-4 pt-20">
        {isPlayable && authorizedSource?.playbackUrl ? (
          /* Real Authorized HTML5 Video Player */
          <div className="w-full max-w-5xl aspect-video bg-black rounded-2xl overflow-hidden shadow-2xl border border-white/10 relative">
            <video
              src={authorizedSource.playbackUrl}
              controls
              autoPlay
              playsInline
              className="w-full h-full object-contain"
            >
              Your browser does not support HTML5 video playback.
            </video>

            {/* Subtle Authorized Stream Overlay Indicator */}
            <div className="absolute bottom-16 right-4 pointer-events-none bg-black/60 backdrop-blur-sm px-2.5 py-1 rounded text-[10px] text-emerald-400 border border-emerald-500/30">
              ✓ Authorized Stream ({authorizedSource.ottPlatform || 'Licensed'})
            </div>
          </div>
        ) : (
          /* NO FAKE PLAYBACK: Clear Legal Warning & Information Message */
          <div className="max-w-xl w-full bg-[#141A29] border border-amber-500/40 rounded-2xl p-6 sm:p-8 text-center space-y-6 shadow-2xl">
            <div className="w-16 h-16 rounded-full bg-amber-500/10 border border-amber-500/30 text-amber-400 flex items-center justify-center mx-auto">
              <ShieldAlert className="w-8 h-8" />
            </div>

            <div className="space-y-2">
              <h2 className="text-xl sm:text-2xl font-black text-white">
                Streaming source is not available.
              </h2>
              <p className="text-amber-300 text-sm font-semibold">
                Available information only — streaming unavailable in this app.
              </p>
            </div>

            <p className="text-gray-300 text-xs sm:text-sm leading-relaxed">
              In accordance with legal distribution rights, commercial movie streaming is only provided when authorized playback arrangements are licensed. This title does not have an authorized in-app playback source configured.
            </p>

            <div className="bg-[#0B0F19] rounded-xl p-4 text-xs text-left space-y-2 border border-white/10 text-gray-400">
              <div className="flex items-center space-x-2 text-white font-bold">
                <Info className="w-4 h-4 text-[#00E5FF]" />
                <span>What can you do?</span>
              </div>
              <ul className="list-disc list-inside space-y-1 text-gray-300">
                <li>Check the Movie Details page to view legal watch availability for India (e.g., Netflix, Prime Video, Aha Video).</li>
                <li>Watch the official trailer preview on the Details screen.</li>
                <li>Administrators can configure licensed streaming sources in the Admin CMS.</li>
              </ul>
            </div>

            <div className="flex flex-col sm:flex-row gap-3 pt-2">
              <button
                onClick={() => navigate(-1)}
                className="flex-1 bg-[#E50914] hover:bg-[#F40612] text-white font-bold py-3 rounded-xl text-sm transition-colors"
              >
                Back to Details
              </button>
              <button
                onClick={() => navigate('/admin')}
                className="flex-1 bg-[#1E273D] hover:bg-[#283550] text-gray-200 font-semibold py-3 rounded-xl text-sm transition-colors border border-white/10"
              >
                Admin Rights Manager
              </button>
            </div>
          </div>
        )}
      </div>

      {/* Footer Info */}
      <div className="p-4 text-center text-xs text-gray-500">
        CinePlus Decoupled Playback Architecture • Powered by TMDB Metadata
      </div>
    </div>
  );
};
