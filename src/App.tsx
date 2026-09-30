import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { Navbar } from './components/Navbar';
import { Footer } from './components/Footer';
import { HomePage } from './pages/HomePage';
import { MoviesPage } from './pages/MoviesPage';
import { TvShowsPage } from './pages/TvShowsPage';
import { SearchPage } from './pages/SearchPage';
import { MovieDetailsPage } from './pages/MovieDetailsPage';
import { TvShowDetailsPage } from './pages/TvShowDetailsPage';
import { WatchPage } from './pages/WatchPage';
import { AdminPage } from './pages/AdminPage';
import { MyListPage } from './pages/MyListPage';

export const App: React.FC = () => {
  return (
    <div className="flex flex-col min-h-screen bg-[#0B0F19] text-white">
      <Navbar />
      <main className="flex-grow">
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/movies" element={<MoviesPage />} />
          <Route path="/tv" element={<TvShowsPage />} />
          <Route path="/search" element={<SearchPage />} />
          <Route path="/movie/:id" element={<MovieDetailsPage />} />
          <Route path="/tv/:id" element={<TvShowDetailsPage />} />
          <Route path="/watch/:id" element={<WatchPage />} />
          <Route path="/admin" element={<AdminPage />} />
          <Route path="/mylist" element={<MyListPage />} />
          <Route path="/profile" element={<Navigate to="/" replace />} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </main>
      <Footer />
    </div>
  );
};

export default App;
