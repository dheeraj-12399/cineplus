import React from 'react';
import { Film, ShieldCheck, Globe, Info } from 'lucide-react';

export const Footer: React.FC = () => {
  return (
    <footer className="bg-[#080B13] border-t border-white/5 pt-12 pb-8 mt-20 text-gray-400 text-xs">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 space-y-8">
        <div className="flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
          <div className="flex items-center space-x-2">
            <span className="text-xl font-black text-[#E50914]">CINE</span>
            <span className="text-xl font-black text-white">PLUS</span>
            <span className="text-[10px] text-gray-400 border border-white/10 px-2 py-0.5 rounded ml-2">
              Region: India (IN)
            </span>
          </div>

          <div className="flex items-center space-x-6 text-gray-400">
            <span className="flex items-center space-x-1">
              <Globe className="w-3.5 h-3.5 text-[#00E5FF]" />
              <span>Real TMDB Live Metadata</span>
            </span>
            <span className="flex items-center space-x-1">
              <ShieldCheck className="w-3.5 h-3.5 text-green-400" />
              <span>Legally Decoupled Playback</span>
            </span>
          </div>
        </div>

        {/* Legal Disclaimer Box */}
        <div className="bg-[#141A29]/60 border border-white/5 rounded-lg p-4 space-y-2">
          <div className="flex items-center space-x-2 text-amber-400 font-semibold text-xs">
            <Info className="w-4 h-4" />
            <span>Metadata & Playback Architecture Notice</span>
          </div>
          <p className="text-[11px] text-gray-300 leading-relaxed">
            Movie and TV show information, posters, cast, crew, and regional watch availability are powered by the official <strong>The Movie Database (TMDB) API</strong>. This product uses the TMDB API but is not endorsed or certified by TMDB. Full commercial playback is strictly decoupled from metadata; full-length streaming is only initiated when an authorized content distributor or licensed stream has been configured in the CMS. For titles without authorized streaming in CinePlus, legal regional watch providers (Netflix, Prime Video, Aha Video, Hotstar, ZEE5) and trailers are presented.
          </p>
        </div>

        <div className="flex flex-col sm:flex-row items-center justify-between pt-6 border-t border-white/5 text-gray-500 text-[11px]">
          <p>© {new Date().getFullYear()} CinePlus Streaming Architecture. All rights reserved.</p>
          <div className="flex space-x-4 mt-2 sm:mt-0">
            <span>Telugu</span>
            <span>•</span>
            <span>Hindi</span>
            <span>•</span>
            <span>Tamil</span>
            <span>•</span>
            <span>Malayalam</span>
            <span>•</span>
            <span>Kannada</span>
            <span>•</span>
            <span>English</span>
          </div>
        </div>
      </div>
    </footer>
  );
};
