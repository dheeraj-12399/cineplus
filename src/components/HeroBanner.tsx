import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Play, Info, Star, Video, Check, Plus, AlertCircle } from 'lucide-react';
import { Movie } from '../types/tmdb';
import { tmdbImage } from '../services/tmdbApi';
import { playbackService } from '../services/playbackService';

interface HeroBannerProps {
  movie: Movie | null;
}

export const HeroBanner: React.FC<HeroBannerProps> = ({ movie }) => {
  const navigate = useNavigate();
  const [isInList, setIsInList] = useState(false);

  if (!movie) {
    return (
      <div className="w-full h-[65vh] md:h-[78vh] bg-[#141A29] animate-pulse flex items-center justify-center">
        <span className="text-gray-500 text-sm">Loading cinematic spotlight...</span>
      </div>
    );
  }

  const isPlayable = playbackService.isAuthorizedForStreaming(movie.id);
  const releaseYear = movie.release_date ? movie.release_date.split('-')[0] : '2024';
  const rating = movie.vote_average ? movie.vote_average.toFixed(1) : '7.8';

  const handlePlayClick = () => {
    navigate(`/watch/${movie.id}?type=movie`);
  };

  const handleMoreInfoClick = () => {
    navigate(`/movie/${movie.id}`);
  };

  return (
    <div className="relative w-full h-[70vh] md:h-[82vh] overflow-hidden">
      {/* High-res Backdrop Image */}
      <img
        src={tmdbImage.backdrop(movie.backdrop_path, 'original')}
        alt={movie.title}
        className="absolute inset-0 w-full h-full object-cover object-center scale-105 transform animate-fade-in"
      />

      {/* Cinematic Scrims */}
      <div className="absolute inset-0 hero-gradient" />
      <div className="absolute inset-0 hero-side-gradient hidden md:block" />

      {/* Content Overlay */}
      <div className="relative max-w-7xl mx-auto h-full px-4 sm:px-6 lg:px-8 flex flex-col justify-end pb-16 sm:pb-20 z-10">
        <div className="max-w-2xl space-y-4">
          {/* Badges */}
          <div className="flex items-center space-x-3 text-xs">
            <span className="bg-[#E50914] text-white font-black px-2.5 py-1 rounded tracking-widest text-[11px] uppercase shadow-md shadow-red-900/40">
              FEATURED SPOTLIGHT
            </span>
            {isPlayable ? (
              <span className="bg-emerald-500/20 text-emerald-400 border border-emerald-500/40 font-semibold px-2 py-0.5 rounded text-[11px]">
                ✓ STREAMING AUTHORIZED
              </span>
            ) : (
              <span className="bg-amber-500/20 text-amber-300 border border-amber-500/40 font-semibold px-2 py-0.5 rounded text-[11px] flex items-center space-x-1">
                <AlertCircle className="w-3 h-3 inline mr-1" />
                <span>THEATRICAL CATALOG</span>
              </span>
            )}
            <div className="flex items-center text-[#FFD700] space-x-1 bg-black/40 px-2 py-0.5 rounded border border-white/10">
              <Star className="w-3.5 h-3.5 fill-[#FFD700]" />
              <span className="font-bold text-white">{rating}</span>
            </div>
            <span className="text-gray-300 font-medium">{releaseYear}</span>
          </div>

          {/* Title */}
          <h1 className="text-3xl sm:text-5xl lg:text-6xl font-black text-white tracking-tight drop-shadow-lg leading-tight">
            {movie.title}
          </h1>

          {/* Synopsis */}
          <p className="text-gray-300 text-xs sm:text-sm lg:text-base line-clamp-3 leading-relaxed drop-shadow">
            {movie.overview || 'Experience the thrilling cinematic journey with real TMDB verified cast and high-definition production details.'}
          </p>

          {/* Action Buttons */}
          <div className="flex flex-wrap items-center gap-3 pt-2">
            {isPlayable ? (
              <button
                onClick={handlePlayClick}
                className="flex items-center space-x-2 bg-[#E50914] hover:bg-[#F40612] text-white font-bold px-6 py-3 rounded-md transition-transform hover:scale-105 shadow-lg shadow-red-600/30 text-sm"
              >
                <Play className="w-5 h-5 fill-white" />
                <span>PLAY NOW</span>
              </button>
            ) : (
              <button
                onClick={handlePlayClick}
                className="flex items-center space-x-2 bg-amber-500/20 border border-amber-500/50 hover:bg-amber-500/30 text-amber-300 font-semibold px-5 py-3 rounded-md transition-all text-sm"
              >
                <Video className="w-4 h-4 text-amber-300" />
                <span>Watch Trailer</span>
              </button>
            )}

            <button
              onClick={() => setIsInList(!isInList)}
              className="flex items-center space-x-2 bg-[#141A29]/80 hover:bg-[#1E273D] border border-white/15 text-white font-semibold px-4 py-3 rounded-md transition-colors text-sm"
            >
              {isInList ? <Check className="w-4 h-4 text-emerald-400" /> : <Plus className="w-4 h-4" />}
              <span>{isInList ? 'In My List' : 'My List'}</span>
            </button>

            <button
              onClick={handleMoreInfoClick}
              className="flex items-center space-x-2 bg-white/10 hover:bg-white/20 text-white font-semibold px-5 py-3 rounded-md transition-colors backdrop-blur-sm border border-white/10 text-sm"
            >
              <Info className="w-4 h-4 text-[#00E5FF]" />
              <span>More Info</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
