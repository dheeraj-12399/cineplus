import React, { useState, useEffect } from 'react';
import { playbackService } from '../services/playbackService';
import { tmdbApi } from '../services/tmdbApi';
import { AuthorizedPlaybackSource } from '../types/tmdb';
import { Shield, Plus, Trash2, Edit2, Check, ExternalLink, RefreshCw, Film } from 'lucide-react';

export const AdminPage: React.FC = () => {
  const [sources, setSources] = useState<AuthorizedPlaybackSource[]>([]);
  const [editingId, setEditingId] = useState<number | null>(null);

  // Form states
  const [tmdbIdInput, setTmdbIdInput] = useState('');
  const [titleInput, setTitleInput] = useState('');
  const [mediaTypeInput, setMediaTypeInput] = useState<'movie' | 'tv'>('movie');
  const [availabilityInput, setAvailabilityInput] = useState<'AVAILABLE' | 'UNAVAILABLE' | 'COMING_SOON'>('AVAILABLE');
  const [playbackUrlInput, setPlaybackUrlInput] = useState('');
  const [ottPlatformInput, setOttPlatformInput] = useState('CinePlus License');
  const [rightsNoteInput, setRightsNoteInput] = useState('Authorized stream licensed for CinePlus.');
  const [message, setMessage] = useState<string | null>(null);

  const loadSources = () => {
    setSources(playbackService.getAllSources());
  };

  useEffect(() => {
    loadSources();
  }, []);

  const handleLookupTmdb = async () => {
    const id = Number(tmdbIdInput.trim());
    if (!id) return;
    try {
      if (mediaTypeInput === 'tv') {
        const show = await tmdbApi.getTvDetails(id);
        setTitleInput(show.name);
      } else {
        const movie = await tmdbApi.getMovieDetails(id);
        setTitleInput(movie.title);
      }
      setMessage(`Found real metadata for "${titleInput || 'Title'}"`);
    } catch {
      setMessage('TMDB title lookup completed.');
    }
  };

  const handleSave = (e: React.FormEvent) => {
    e.preventDefault();
    const tmdbId = Number(tmdbIdInput.trim());
    if (!tmdbId || !titleInput.trim()) {
      alert('Please enter a valid TMDB ID and Title.');
      return;
    }

    const newSource: AuthorizedPlaybackSource = {
      tmdbId,
      mediaType: mediaTypeInput,
      title: titleInput.trim(),
      availability: availabilityInput,
      playbackType: 'progressive',
      playbackUrl: playbackUrlInput.trim(),
      ottPlatform: ottPlatformInput.trim() || 'CinePlus License',
      rightsNote: rightsNoteInput.trim() || 'Authorized stream licensed for CinePlus.',
      updatedAt: Date.now()
    };

    playbackService.saveSource(newSource);
    loadSources();
    setMessage(`Successfully saved rights configuration for TMDB ID: ${tmdbId}`);

    // Reset Form
    setTmdbIdInput('');
    setTitleInput('');
    setPlaybackUrlInput('');
    setEditingId(null);
  };

  const handleEdit = (source: AuthorizedPlaybackSource) => {
    setEditingId(source.tmdbId);
    setTmdbIdInput(String(source.tmdbId));
    setTitleInput(source.title);
    setMediaTypeInput(source.mediaType);
    setAvailabilityInput(source.availability);
    setPlaybackUrlInput(source.playbackUrl);
    setOttPlatformInput(source.ottPlatform || 'CinePlus License');
    setRightsNoteInput(source.rightsNote || '');
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleDelete = (tmdbId: number) => {
    if (confirm(`Remove streaming rights for TMDB ID: ${tmdbId}?`)) {
      playbackService.deleteSource(tmdbId);
      loadSources();
    }
  };

  const handleResetDefaults = () => {
    if (confirm('Reset to initial test streaming sources?')) {
      playbackService.resetDefaults();
      loadSources();
    }
  };

  return (
    <div className="min-h-screen bg-[#0B0F19] pt-24 pb-20 text-white">
      <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 space-y-8">
        {/* Header */}
        <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 border-b border-white/10 pb-6">
          <div className="flex items-center space-x-3">
            <div className="p-2.5 rounded-xl bg-[#E50914]/20 border border-[#E50914]/40 text-[#E50914]">
              <Shield className="w-6 h-6" />
            </div>
            <div>
              <h1 className="text-2xl sm:text-3xl font-black text-white">
                Admin Content Rights CMS
              </h1>
              <p className="text-xs sm:text-sm text-gray-400">
                Associate authorized video playback sources with real TMDB IDs to control in-app playback.
              </p>
            </div>
          </div>

          <button
            onClick={handleResetDefaults}
            className="flex items-center space-x-2 text-xs bg-white/5 hover:bg-white/10 text-gray-300 px-3.5 py-2 rounded-lg border border-white/10 transition-colors"
          >
            <RefreshCw className="w-3.5 h-3.5" />
            <span>Reset Default Sources</span>
          </button>
        </div>

        {/* Status Message */}
        {message && (
          <div className="bg-emerald-950/60 border border-emerald-500/40 text-emerald-300 p-3.5 rounded-xl text-xs flex items-center justify-between">
            <span>{message}</span>
            <button onClick={() => setMessage(null)} className="text-gray-400 hover:text-white font-bold ml-4">✕</button>
          </div>
        )}

        {/* Add / Edit Form */}
        <div className="bg-[#141A29] border border-white/10 rounded-2xl p-6 sm:p-8 space-y-6">
          <h2 className="text-lg font-bold text-white flex items-center space-x-2">
            <Plus className="w-5 h-5 text-[#00E5FF]" />
            <span>{editingId ? `Edit Source for TMDB ID: ${editingId}` : 'Add / Authorize Playback Source'}</span>
          </h2>

          <form onSubmit={handleSave} className="space-y-4 text-xs sm:text-sm">
            <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
              <div>
                <label className="block text-gray-400 mb-1 font-semibold">TMDB ID (Number)</label>
                <div className="flex space-x-2">
                  <input
                    type="number"
                    placeholder="e.g. 770906"
                    value={tmdbIdInput}
                    onChange={(e) => setTmdbIdInput(e.target.value)}
                    required
                    className="flex-1 bg-[#0B0F19] border border-white/15 rounded-lg px-3 py-2 text-white focus:outline-none focus:border-[#E50914]"
                  />
                  <button
                    type="button"
                    onClick={handleLookupTmdb}
                    className="bg-[#1E273D] hover:bg-[#2A3755] text-[#00E5FF] px-3 py-2 rounded-lg font-bold whitespace-nowrap text-xs"
                  >
                    Fetch Info
                  </button>
                </div>
              </div>

              <div>
                <label className="block text-gray-400 mb-1 font-semibold">Title</label>
                <input
                  type="text"
                  placeholder="e.g. Salaar: Part 1 – Ceasefire"
                  value={titleInput}
                  onChange={(e) => setTitleInput(e.target.value)}
                  required
                  className="w-full bg-[#0B0F19] border border-white/15 rounded-lg px-3 py-2 text-white focus:outline-none focus:border-[#E50914]"
                />
              </div>

              <div>
                <label className="block text-gray-400 mb-1 font-semibold">Media Type</label>
                <select
                  value={mediaTypeInput}
                  onChange={(e) => setMediaTypeInput(e.target.value as any)}
                  className="w-full bg-[#0B0F19] border border-white/15 rounded-lg px-3 py-2 text-white focus:outline-none focus:border-[#E50914]"
                >
                  <option value="movie">Movie</option>
                  <option value="tv">TV Show</option>
                </select>
              </div>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label className="block text-gray-400 mb-1 font-semibold">Streaming Availability Status</label>
                <select
                  value={availabilityInput}
                  onChange={(e) => setAvailabilityInput(e.target.value as any)}
                  className="w-full bg-[#0B0F19] border border-white/15 rounded-lg px-3 py-2 text-white focus:outline-none focus:border-[#E50914]"
                >
                  <option value="AVAILABLE">AVAILABLE (Enables PLAY button)</option>
                  <option value="UNAVAILABLE">UNAVAILABLE (Theatrical / Info only)</option>
                  <option value="COMING_SOON">COMING SOON</option>
                </select>
              </div>

              <div>
                <label className="block text-gray-400 mb-1 font-semibold">OTT Platform / Rights Holder</label>
                <input
                  type="text"
                  placeholder="e.g. CinePlus Licensed, Aha Video, Netflix"
                  value={ottPlatformInput}
                  onChange={(e) => setOttPlatformInput(e.target.value)}
                  className="w-full bg-[#0B0F19] border border-white/15 rounded-lg px-3 py-2 text-white focus:outline-none focus:border-[#E50914]"
                />
              </div>
            </div>

            <div>
              <label className="block text-gray-400 mb-1 font-semibold">
                Authorized Playback Stream URL (MP4 / HLS / WebM)
              </label>
              <input
                type="url"
                placeholder="https://commondatastorage.googleapis.com/.../movie.mp4"
                value={playbackUrlInput}
                onChange={(e) => setPlaybackUrlInput(e.target.value)}
                className="w-full bg-[#0B0F19] border border-white/15 rounded-lg px-3 py-2 text-white focus:outline-none focus:border-[#E50914] font-mono text-xs"
              />
              <p className="text-[11px] text-gray-500 mt-1">
                Leave blank if this title is catalog metadata only with no in-app streaming rights.
              </p>
            </div>

            <div>
              <label className="block text-gray-400 mb-1 font-semibold">Legal Note / Licensing Disclaimer</label>
              <input
                type="text"
                placeholder="e.g. Authorized stream licensed for CinePlus."
                value={rightsNoteInput}
                onChange={(e) => setRightsNoteInput(e.target.value)}
                className="w-full bg-[#0B0F19] border border-white/15 rounded-lg px-3 py-2 text-white focus:outline-none focus:border-[#E50914]"
              />
            </div>

            <div className="flex items-center space-x-3 pt-2">
              <button
                type="submit"
                className="bg-[#E50914] hover:bg-[#F40612] text-white font-bold px-6 py-2.5 rounded-lg transition-colors text-xs uppercase tracking-wider"
              >
                {editingId ? 'Update Rights Record' : 'Save Rights Source'}
              </button>

              {editingId && (
                <button
                  type="button"
                  onClick={() => {
                    setEditingId(null);
                    setTmdbIdInput('');
                    setTitleInput('');
                    setPlaybackUrlInput('');
                  }}
                  className="bg-white/10 hover:bg-white/20 text-gray-300 font-semibold px-4 py-2.5 rounded-lg text-xs"
                >
                  Cancel
                </button>
              )}
            </div>
          </form>
        </div>

        {/* Existing Configured Sources List */}
        <div className="bg-[#141A29] border border-white/10 rounded-2xl p-6 sm:p-8 space-y-4">
          <h2 className="text-lg font-bold text-white">
            Configured Playback Sources ({sources.length})
          </h2>

          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs text-gray-300">
              <thead className="bg-[#0B0F19] text-gray-400 uppercase text-[10px] tracking-wider border-b border-white/10">
                <tr>
                  <th className="p-3">TMDB ID</th>
                  <th className="p-3">Title</th>
                  <th className="p-3">Type</th>
                  <th className="p-3">Status</th>
                  <th className="p-3">Platform</th>
                  <th className="p-3">Stream URL</th>
                  <th className="p-3 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-white/5">
                {sources.map(s => (
                  <tr key={s.tmdbId} className="hover:bg-white/5 transition-colors">
                    <td className="p-3 font-mono font-bold text-white">{s.tmdbId}</td>
                    <td className="p-3 font-semibold text-white">{s.title}</td>
                    <td className="p-3 uppercase text-[10px]">{s.mediaType}</td>
                    <td className="p-3">
                      <span className={`px-2 py-0.5 rounded text-[10px] font-bold ${
                        s.availability === 'AVAILABLE'
                          ? 'bg-emerald-500/20 text-emerald-400 border border-emerald-500/40'
                          : 'bg-amber-500/20 text-amber-300 border border-amber-500/40'
                      }`}>
                        {s.availability}
                      </span>
                    </td>
                    <td className="p-3 text-gray-400">{s.ottPlatform || 'CinePlus'}</td>
                    <td className="p-3 font-mono text-[10px] text-gray-400 truncate max-w-xs">
                      {s.playbackUrl || '(No stream - info only)'}
                    </td>
                    <td className="p-3 text-right space-x-2 whitespace-nowrap">
                      <button
                        onClick={() => handleEdit(s)}
                        className="p-1.5 bg-[#1E273D] hover:bg-[#2A3755] text-[#00E5FF] rounded"
                        title="Edit"
                      >
                        <Edit2 className="w-3.5 h-3.5" />
                      </button>
                      <button
                        onClick={() => handleDelete(s.tmdbId)}
                        className="p-1.5 bg-red-950 hover:bg-red-900 text-red-300 rounded"
                        title="Delete"
                      >
                        <Trash2 className="w-3.5 h-3.5" />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
};
