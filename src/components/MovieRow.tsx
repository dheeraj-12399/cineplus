import React, { useRef } from 'react';
import { Link } from 'react-router-dom';
import { ChevronLeft, ChevronRight } from 'lucide-react';
import { Movie, TvShow } from '../types/tmdb';
import { MediaCard } from './MediaCard';

interface MovieRowProps {
  title: string;
  items: (Movie | TvShow)[];
  mediaType?: 'movie' | 'tv';
  seeAllLink?: string;
  badge?: string;
  icon?: string;
}

export const MovieRow: React.FC<MovieRowProps> = ({
  title,
  items,
  mediaType = 'movie',
  seeAllLink,
  badge,
  icon
}) => {
  const rowRef = useRef<HTMLDivElement>(null);

  const scroll = (direction: 'left' | 'right') => {
    if (rowRef.current) {
      const { scrollLeft, clientWidth } = rowRef.current;
      const scrollAmount = clientWidth * 0.75;
      rowRef.current.scrollTo({
        left: direction === 'left' ? scrollLeft - scrollAmount : scrollLeft + scrollAmount,
        behavior: 'smooth'
      });
    }
  };

  if (!items || items.length === 0) return null;

  return (
    <div className="relative py-4 space-y-3">
      {/* Row Header */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex items-center justify-between">
        <div className="flex items-center space-x-2.5">
          {icon && <span className="text-lg">{icon}</span>}
          <h2 className="text-lg sm:text-xl font-bold text-white tracking-wide">
            {title}
          </h2>
          {badge && (
            <span className="text-[10px] bg-[#E50914]/20 border border-[#E50914]/40 text-[#E50914] font-black px-2 py-0.5 rounded uppercase">
              {badge}
            </span>
          )}
        </div>

        {seeAllLink && (
          <Link
            to={seeAllLink}
            className="text-xs font-semibold text-[#00E5FF] hover:underline flex items-center"
          >
            Explore All
          </Link>
        )}
      </div>

      {/* Row Slider Container */}
      <div className="relative group max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Left Arrow */}
        <button
          onClick={() => scroll('left')}
          className="absolute left-2 top-1/2 -translate-y-1/2 z-20 w-10 h-10 rounded-full bg-black/70 hover:bg-[#E50914] text-white flex items-center justify-center opacity-0 group-hover:opacity-100 transition-all shadow-xl backdrop-blur-sm"
          aria-label="Scroll left"
        >
          <ChevronLeft className="w-6 h-6" />
        </button>

        {/* Scrollable Track */}
        <div
          ref={rowRef}
          className="flex space-x-3 sm:space-x-4 overflow-x-auto no-scrollbar scroll-smooth py-2"
        >
          {items.map((item) => (
            <MediaCard key={item.id} item={item} mediaType={mediaType} />
          ))}
        </div>

        {/* Right Arrow */}
        <button
          onClick={() => scroll('right')}
          className="absolute right-2 top-1/2 -translate-y-1/2 z-20 w-10 h-10 rounded-full bg-black/70 hover:bg-[#E50914] text-white flex items-center justify-center opacity-0 group-hover:opacity-100 transition-all shadow-xl backdrop-blur-sm"
          aria-label="Scroll right"
        >
          <ChevronRight className="w-6 h-6" />
        </button>
      </div>
    </div>
  );
};
