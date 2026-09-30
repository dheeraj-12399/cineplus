import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { tmdbApi, tmdbImage } from '../services/tmdbApi';
import { playbackService } from '../services/playbackService';
import { TvShow, SeasonDetails } from '../types/tmdb';
import {
  Play,
  Video,
  Star,
  Tv,
  ArrowLeft,
  Loader2,
  Calendar,
  Layers,
  Clock,
  AlertTriangle,
  Check,
  Plus
} from 'lucide-react';

export const TvShowDetailsPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [show, setShow] = useState<TvShow | null>(null);
  const [selectedSeason, setSelectedSeason] = useState<number>(1);
  const [seasonDetails, setSeasonDetails] = useState<SeasonDetails | null>(null);
  const [loading, setLoading] = useState(true);
  const [loadingSeason, setLoadingSeason] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [isInList, setIsInList] = useState(false);

  useEffect(() => {
    const fetchShow = async () => {
      if (!id) return;
      setLoading(true);
      setError(null);
      try {
        const data = await tmdbApi.getTvDetails(Number(id));
        setShow(data);

        // Find initial season (skip season 0 specials if season 1 exists)
        const firstSeason = data.seasons?.find(s => s.season_number === 1)?.season_number || data.seasons?.[0]?.season_number || 1;
        setSelectedSeason(firstSeason);
      } catch (err: any) {
        setError(err?.message || 'Failed to retrieve TV show details from TMDB.');
      } finally {
        setLoading(false);
      }
    };

    fetchShow();
    window.scrollTo(0, 0);
  }, [id]);

  // Load real episodes when season changes
  useEffect(() => {
    const fetchSeason = async () => {
      if (!id || !selectedSeason) return;
      setLoadingSeason(true);
      try {
        const seasonData = await tmdbApi.getTvSeasonDetails(Number(id), selectedSeason);
        setSeasonDetails(seasonData);
      } catch (err) {
        console.error('Failed to load season details:', err);
      } finally {
        setLoadingSeason(false);
      }
    };

    fetchSeason();
  }, [id, selectedSeason]);

  if (loading) {
    return (
      <div className="min-h-screen bg-[#0B0F19] flex flex-col items-center justify-center space-y-3">
        <Loader2 className="w-12 h-12 text-[#E50914] animate-spin" />
        <p className="text-gray-400 text-sm">Retrieving real TV show metadata from TMDB...</p>
      </div>
    );
  }

  if (error || !show) {
    return (
      <div className="min-h-screen bg-[#0B0F19] flex flex-col items-center justify-center px-4 text-center space-y-4">
        <AlertTriangle className="w-12 h-12 text-[#E50914]" />
        <h2 className="text-xl font-bold text-white">TV Show Not Available</h2>
        <p className="text-gray-400 text-sm max-w-md">{error || 'Could not locate this TV show in TMDB.'}</p>
        <button onClick={() => navigate(-1)} className="bg-[#141A29] text-white px-5 py-2.5 rounded-md font-semibold text-sm">
          Go Back
        </button>
      </div>
    );
  }

  const isPlayable = playbackService.isAuthorizedForStreaming(show.id);
  const releaseYear = show.first_air_date ? show.first_air_date.split('-')[0] : '2024';
  const creators = show.created_by?.map(c => c.name).join(', ') || 'Not Listed';
  const validSeasons = show.seasons?.filter(s => s.season_number > 0) || [];

  return (
    <div className="min-h-screen bg-[#0B0F19] pb-24 text-white">
      {/* Backdrop */}
      <div className="relative w-full h-[50vh] md:h-[65vh] overflow-hidden">
        <img
          src={tmdbImage.backdrop(show.backdrop_path, 'original')}
          alt={show.name}
          className="w-full h-full object-cover object-top"
        />
        <div className="absolute inset-0 bg-gradient-to-t from-[#0B0F19] via-[#0B0F19]/70 to-transparent" />
        <div className="absolute inset-0 bg-gradient-to-r from-[#0B0F19] via-[#0B0F19]/50 to-transparent hidden md:block" />

        <button
          onClick={() => navigate(-1)}
          className="absolute top-20 left-4 sm:left-8 z-30 p-2.5 rounded-full bg-black/60 hover:bg-[#E50914] text-white transition-colors"
        >
          <ArrowLeft className="w-5 h-5" />
        </button>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 -mt-36 sm:-mt-44 relative z-20 space-y-10">
        {/* Info header */}
        <div className="flex flex-col md:flex-row gap-8 items-start">
          <div className="w-44 sm:w-56 md:w-64 flex-shrink-0 mx-auto md:mx-0">
            <div className="rounded-xl overflow-hidden shadow-2xl border-2 border-white/10 bg-[#141A29]">
              <img src={tmdbImage.poster(show.poster_path, 'w500')} alt={show.name} className="w-full h-auto object-cover" />
            </div>
          </div>

          <div className="flex-1 space-y-4">
            <div>
              <div className="flex flex-wrap items-center gap-2 text-xs mb-2">
                <span className="bg-[#E50914] text-white font-bold px-2 py-0.5 rounded text-[10px] uppercase">
                  TV Series
                </span>
                <span className="text-gray-400 font-semibold">{releaseYear}</span>
                <span>•</span>
                <span className="text-gray-400">{validSeasons.length} Season{validSeasons.length > 1 ? 's' : ''}</span>
                <span>•</span>
                <div className="flex items-center text-[#FFD700] space-x-1 font-bold">
                  <Star className="w-3.5 h-3.5 fill-[#FFD700]" />
                  <span>{show.vote_average?.toFixed(1) || '7.5'}</span>
                </div>
              </div>

              <h1 className="text-3xl sm:text-4xl lg:text-5xl font-black text-white tracking-tight leading-tight">
                {show.name}
              </h1>

              {show.original_name && show.original_name !== show.name && (
                <p className="text-sm text-[#00E5FF] font-medium mt-1">
                  Original Title: {show.original_name}
                </p>
              )}
            </div>

            {/* Genres */}
            {show.genres && (
              <div className="flex flex-wrap gap-2">
                {show.genres.map(g => (
                  <span key={g.id} className="text-xs bg-[#141A29] border border-white/10 text-gray-300 px-3 py-1 rounded-full">
                    {g.name}
                  </span>
                ))}
              </div>
            )}

            {/* Legal streaming disclaimer */}
            <div className={`p-4 rounded-xl border text-xs sm:text-sm ${
              isPlayable ? 'bg-emerald-950/40 border-emerald-500/40 text-emerald-300' : 'bg-amber-950/40 border-amber-500/30 text-amber-200'
            }`}>
              <p className="font-bold">
                {isPlayable ? '✓ IN-APP STREAMING SOURCE AVAILABLE' : 'Available information only — streaming unavailable in this app.'}
              </p>
              <p className="text-gray-300 text-xs mt-1">
                {isPlayable
                  ? 'Authorized episodic streams are licensed for CinePlus subscribers.'
                  : 'Full season and episode guides are retrieved from TMDB. Full commercial playback is not licensed in-app.'}
              </p>
            </div>

            {/* Synopsis */}
            <p className="text-gray-300 text-sm leading-relaxed">
              {show.overview || 'No synopsis provided by metadata provider.'}
            </p>

            <div className="text-xs text-gray-400 border-t border-white/10 pt-3">
              <span className="font-semibold text-gray-300">Created by: </span>
              <span className="text-white">{creators}</span>
            </div>
          </div>
        </div>

        {/* Season Selector Tabs */}
        {validSeasons.length > 0 && (
          <div className="space-y-4 pt-4">
            <div className="flex items-center space-x-2 text-white">
              <Layers className="w-5 h-5 text-[#00E5FF]" />
              <h2 className="text-xl font-bold">Select Season</h2>
            </div>

            <div className="flex flex-wrap gap-2">
              {validSeasons.map(s => {
                const isSelected = selectedSeason === s.season_number;
                return (
                  <button
                    key={s.id}
                    onClick={() => setSelectedSeason(s.season_number)}
                    className={`px-4 py-2 rounded-lg font-bold text-xs sm:text-sm transition-all ${
                      isSelected
                        ? 'bg-[#E50914] text-white shadow-md shadow-red-900/40'
                        : 'bg-[#141A29] text-gray-300 hover:bg-[#1E273D] border border-white/10'
                    }`}
                  >
                    Season {s.season_number} ({s.episode_count} Episodes)
                  </button>
                );
              })}
            </div>
          </div>
        )}

        {/* Real Episodes List */}
        <div className="space-y-4">
          <div className="flex items-center justify-between">
            <h3 className="text-lg font-bold text-white">
              Season {selectedSeason} Episodes ({seasonDetails?.episodes?.length || 0})
            </h3>
          </div>

          {loadingSeason ? (
            <div className="py-12 flex justify-center">
              <Loader2 className="w-8 h-8 text-[#E50914] animate-spin" />
            </div>
          ) : !seasonDetails?.episodes || seasonDetails.episodes.length === 0 ? (
            <p className="text-gray-400 text-sm py-4">No episode guide available for this season.</p>
          ) : (
            <div className="space-y-3">
              {seasonDetails.episodes.map(ep => (
                <div
                  key={ep.id}
                  className="bg-[#141A29] border border-white/5 hover:border-white/20 rounded-xl p-3 sm:p-4 flex flex-col sm:flex-row gap-4 items-start transition-colors"
                >
                  {/* Episode Still */}
                  <div className="relative w-full sm:w-48 aspect-video flex-shrink-0 bg-gray-900 rounded-lg overflow-hidden border border-white/10">
                    <img
                      src={tmdbImage.backdrop(ep.still_path, 'w780')}
                      alt={ep.name}
                      className="w-full h-full object-cover"
                      loading="lazy"
                    />
                    <span className="absolute bottom-1 right-1 bg-black/80 text-[10px] text-gray-200 px-1.5 py-0.5 rounded">
                      {ep.runtime ? `${ep.runtime}m` : '45m'}
                    </span>
                  </div>

                  {/* Episode Info */}
                  <div className="flex-1 space-y-1">
                    <div className="flex items-center justify-between">
                      <h4 className="text-sm sm:text-base font-bold text-white">
                        {ep.episode_number}. {ep.name}
                      </h4>
                      {ep.vote_average > 0 && (
                        <span className="text-xs text-[#FFD700] font-semibold flex items-center space-x-1">
                          <Star className="w-3 h-3 fill-[#FFD700]" />
                          <span>{ep.vote_average.toFixed(1)}</span>
                        </span>
                      )}
                    </div>
                    <p className="text-xs text-gray-400 line-clamp-3 leading-relaxed">
                      {ep.overview || 'No episode synopsis provided by metadata provider.'}
                    </p>
                    {ep.air_date && (
                      <p className="text-[10px] text-gray-500 pt-1">
                        Air Date: {ep.air_date}
                      </p>
                    )}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
