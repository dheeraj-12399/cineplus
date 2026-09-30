import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { Search, Film, Tv, Heart, Shield, Menu, X, Sparkles } from 'lucide-react';

export const Navbar: React.FC = () => {
  const [isScrolled, setIsScrolled] = useState(false);
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    const handleScroll = () => {
      setIsScrolled(window.scrollY > 40);
    };
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (searchQuery.trim()) {
      navigate(`/search?q=${encodeURIComponent(searchQuery.trim())}`);
      setMobileMenuOpen(false);
    }
  };

  const navLinks = [
    { name: 'Home', path: '/' },
    { name: 'Telugu', path: '/movies?lang=Telugu' },
    { name: 'Movies', path: '/movies' },
    { name: 'TV Shows', path: '/tv' },
    { name: 'My List', path: '/mylist' },
    { name: 'Admin CMS', path: '/admin' }
  ];

  return (
    <header
      className={`fixed top-0 left-0 right-0 z-50 transition-all duration-300 ${
        isScrolled ? 'bg-[#0B0F19]/95 backdrop-blur-md shadow-lg shadow-black/40 py-3 border-b border-white/5' : 'bg-gradient-to-b from-[#0B0F19] to-transparent py-4'
      }`}
    >
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex items-center justify-between">
        {/* Brand Logo */}
        <div className="flex items-center space-x-8">
          <Link to="/" className="flex items-center space-x-1 group">
            <span className="text-2xl font-black tracking-wider text-[#E50914] group-hover:scale-105 transition-transform">
              CINE
            </span>
            <span className="text-2xl font-black tracking-wider text-white">
              PLUS
            </span>
            <span className="ml-2 text-[10px] bg-[#E50914] text-white px-1.5 py-0.5 rounded font-bold uppercase tracking-widest">
              TMDB
            </span>
          </Link>

          {/* Desktop Navigation */}
          <nav className="hidden md:flex items-center space-x-6 text-sm font-medium">
            {navLinks.map((link) => {
              const isActive = location.pathname === link.path || (link.path.includes('?') && location.search.includes(link.path.split('?')[1]));
              return (
                <Link
                  key={link.name}
                  to={link.path}
                  className={`transition-colors hover:text-white ${
                    isActive ? 'text-white font-bold border-b-2 border-[#E50914] pb-1' : 'text-gray-300'
                  }`}
                >
                  {link.name}
                </Link>
              );
            })}
          </nav>
        </div>

        {/* Right Action Icons & Search */}
        <div className="hidden md:flex items-center space-x-4">
          <form onSubmit={handleSearchSubmit} className="relative">
            <input
              type="text"
              placeholder="Search titles, actors, directors..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-56 lg:w-72 bg-[#141A29]/90 border border-white/10 text-white text-xs rounded-full pl-9 pr-4 py-2 focus:outline-none focus:border-[#E50914] focus:w-80 transition-all placeholder-gray-400"
            />
            <Search className="w-4 h-4 text-gray-400 absolute left-3 top-2.5" />
          </form>

          <Link
            to="/admin"
            className="p-2 text-gray-300 hover:text-[#00E5FF] transition-colors rounded-full hover:bg-white/5"
            title="Admin Content Rights CMS"
          >
            <Shield className="w-5 h-5" />
          </Link>

          <Link
            to="/mylist"
            className="p-2 text-gray-300 hover:text-[#E50914] transition-colors rounded-full hover:bg-white/5"
            title="My Saved List"
          >
            <Heart className="w-5 h-5" />
          </Link>
        </div>

        {/* Mobile Hamburger Button */}
        <div className="flex md:hidden items-center space-x-2">
          <Link to="/search" className="p-2 text-gray-300 hover:text-white">
            <Search className="w-5 h-5" />
          </Link>
          <button
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
            className="p-2 text-gray-300 hover:text-white focus:outline-none"
            aria-label="Toggle menu"
          >
            {mobileMenuOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
          </button>
        </div>
      </div>

      {/* Mobile Drawer Menu */}
      {mobileMenuOpen && (
        <div className="md:hidden bg-[#0B0F19]/98 border-b border-white/10 px-4 pt-3 pb-6 space-y-4">
          <form onSubmit={handleSearchSubmit} className="relative mb-4">
            <input
              type="text"
              placeholder="Search movies, TV shows, actors..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full bg-[#141A29] border border-white/10 text-white text-sm rounded-lg pl-10 pr-4 py-2.5 focus:outline-none focus:border-[#E50914]"
            />
            <Search className="w-4 h-4 text-gray-400 absolute left-3 top-3.5" />
          </form>

          <nav className="flex flex-col space-y-3">
            {navLinks.map((link) => (
              <Link
                key={link.name}
                to={link.path}
                onClick={() => setMobileMenuOpen(false)}
                className="text-gray-200 hover:text-white text-base font-medium py-1 px-2 rounded hover:bg-white/5"
              >
                {link.name}
              </Link>
            ))}
          </nav>
        </div>
      )}
    </header>
  );
};
