import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Heart, Film, ArrowRight } from 'lucide-react';
import { Movie, TvShow } from '../types/tmdb';
import { MediaCard } from '../components/MediaCard';

export const MyListPage: React.FC = () => {
  const [items, setItems] = useState<(Movie | TvShow)[]>([]);

  useEffect(() => {
    // In demo / client-side, read from localStorage or show curated list
    try {
      const stored = localStorage.getItem('cineplus_saved_list');
      if (stored) {
        setItems(JSON.parse(stored));
      }
    } catch {
      setItems([]);
    }
  }, []);

  return (
    <div className="min-h-screen bg-[#0B0F19] pt-24 pb-20 text-white">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 space-y-6">
        <div className="flex items-center space-x-3 border-b border-white/10 pb-4">
          <Heart className="w-7 h-7 text-[#E50914] fill-[#E50914]" />
          <div>
            <h1 className="text-2xl sm:text-3xl font-black">My Saved List</h1>
            <p className="text-xs sm:text-sm text-gray-400">
              Personalized watchlist and favorites tracked locally.
            </p>
          </div>
        </div>

        {items.length === 0 ? (
          <div className="bg-[#141A29] border border-white/10 rounded-2xl p-12 text-center max-w-lg mx-auto space-y-4 my-12">
            <Film className="w-12 h-12 text-gray-500 mx-auto" />
            <h3 className="text-lg font-bold text-white">Your list is empty</h3>
            <p className="text-xs text-gray-400">
              Explore Telugu Cinema, Trending Movies, or TV Series and tap "Add to My List" to bookmark titles.
            </p>
            <Link
              to="/movies?lang=Telugu"
              className="inline-flex items-center space-x-2 bg-[#E50914] hover:bg-[#F40612] text-white font-bold px-6 py-2.5 rounded-lg text-xs uppercase tracking-wider transition-colors"
            >
              <span>Explore Telugu Movies</span>
              <ArrowRight className="w-4 h-4" />
            </Link>
          </div>
        ) : (
          <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-6 gap-3 sm:gap-4">
            {items.map(item => (
              <MediaCard key={item.id} item={item} widthClass="w-full" />
            ))}
          </div>
        )}
      </div>
    </div>
  );
};
