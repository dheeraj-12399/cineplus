import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Star, Play, AlertCircle } from 'lucide-react';
import { Movie, TvShow } from '../types/tmdb';
import { tmdbImage } from '../services/tmdbApi';
import { playbackService } from '../services/playbackService';

interface MediaCardProps {
  item: Movie | TvShow;
  mediaType?: 'movie' | 'tv';
  widthClass?: string;
}

export const MediaCard: React.FC<MediaCardProps> = ({ item, mediaType = 'movie', widthClass = 'w-36 sm:w-44 md:w-48' }) => {
  const navigate = useNavigate();

  const title = 'title' in item ? item.title : item.name;
  const releaseDate = 'release_date' in item ? item.release_date : item.first_air_date;
  const releaseYear = releaseDate ? releaseDate.split('-')[0] : '2024';
  const rating = item.vote_average ? item.vote_average.toFixed(1) : '7.5';
  const isPlayable = playbackService.isAuthorizedForStreaming(item.id);

  const handleClick = () => {
    navigate(`/${mediaType}/${item.id}`);
  };

  return (
    <div
      onClick={handleClick}
      className={`group relative flex-shrink-0 ${widthClass} cursor-pointer select-none rounded-lg overflow-hidden bg-[#141A29] border border-white/5 hover:border-white/20 transition-all duration-300 hover:scale-[1.03] hover:shadow-xl hover:shadow-black/60`}
    >
      {/* Poster Image */}
      <div className="relative aspect-[2/3] w-full overflow-hidden bg-[#10141E]">
        <img
          src={tmdbImage.poster(item.poster_path, 'w500')}
          alt={title}
          loading="lazy"
          className="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105"
        />

        {/* Hover Overlay with Quick Play / Info */}
        <div className="absolute inset-0 bg-black/60 opacity-0 group-hover:opacity-100 transition-opacity duration-300 flex flex-col items-center justify-center p-3 text-center space-y-2">
          {isPlayable ? (
            <div className="w-10 h-10 rounded-full bg-[#E50914] text-white flex items-center justify-center shadow-lg transform transition-transform group-hover:scale-110">
              <Play className="w-5 h-5 fill-white ml-0.5" />
            </div>
          ) : (
            <div className="w-10 h-10 rounded-full bg-amber-500/30 border border-amber-500/60 text-amber-300 flex items-center justify-center">
              <AlertCircle className="w-5 h-5" />
            </div>
          )}

          <p className="text-[11px] font-medium text-gray-200 line-clamp-2">
            {title}
          </p>

          <span className="text-[10px] text-gray-400">
            {isPlayable ? '▶ Play Stream' : 'View Details'}
          </span>
        </div>

        {/* Streaming Rights Badge */}
        <div className="absolute top-2 left-2 z-10">
          {isPlayable ? (
            <span className="bg-emerald-600/90 text-white text-[9px] font-bold px-1.5 py-0.5 rounded shadow">
              STREAM
            </span>
          ) : (
            <span className="bg-black/75 text-amber-300 border border-amber-500/30 text-[9px] font-semibold px-1.5 py-0.5 rounded">
              INFO
            </span>
          )}
        </div>

        {/* Rating Badge */}
        <div className="absolute top-2 right-2 z-10 flex items-center space-x-1 bg-black/70 backdrop-blur-sm text-[#FFD700] text-[10px] font-bold px-1.5 py-0.5 rounded border border-white/10">
          <Star className="w-3 h-3 fill-[#FFD700]" />
          <span>{rating}</span>
        </div>
      </div>

      {/* Card Info Footer */}
      <div className="p-2.5">
        <h3 className="text-xs sm:text-sm font-semibold text-white truncate group-hover:text-[#00E5FF] transition-colors">
          {title}
        </h3>
        <div className="flex items-center justify-between text-[11px] text-gray-400 mt-1">
          <span>{releaseYear}</span>
          <span className="text-[10px] uppercase font-bold text-gray-500">
            {item.original_language?.toUpperCase() || 'EN'}
          </span>
        </div>
      </div>
    </div>
  );
};
