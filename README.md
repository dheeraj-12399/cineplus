# CinePlus - Streaming Movie & TV Platform

A modern, production-ready movie and TV streaming web application built with **React**, **TypeScript**, and **Vite**, powered by real **TMDB API** metadata and a decoupled **authorized playback architecture**.

---

## 🚀 Vercel Deployment Settings

This project is configured out-of-the-box for **Vercel** zero-config deployment:

- **Framework Preset**: `Vite`
- **Root Directory**: `./` (Root of the repository)
- **Build Command**: `npm run build`
- **Output Directory**: `dist`
- **Install Command**: `npm install`

### Environment Variables
In your Vercel Project Settings > **Environment Variables**, add:

| Key | Value | Description |
|---|---|---|
| `VITE_TMDB_API_KEY` | *your_tmdb_api_key* | Official The Movie Database (TMDB) v3 API key |

*(Note: The app includes a built-in public educational fallback key, but you should set your own TMDB API key in production).*

---

## 📁 Project Structure

```
cineplus/
├── package.json              # Production dependencies & build scripts at repository root
├── index.html                # Vite HTML entry point
├── vite.config.ts            # Vite configuration with React plugin
├── vercel.json               # Vercel configuration with SPA route fallback rewrites
├── tsconfig.json             # TypeScript configuration
├── tsconfig.node.json        # Vite node configuration
├── .env.example              # Environment variable documentation placeholder
├── .gitignore                # Git exclusions
├── dist/                     # Production build artifacts
│   ├── index.html
│   └── assets/
└── src/
    ├── main.tsx              # React DOM entry point
    ├── App.tsx               # Main SPA routing & layout
    ├── index.css             # Dark cinematic theme & scrollbars
    ├── types/
    │   └── tmdb.ts           # TypeScript models for Movies, TV, and Playback
    ├── services/
    │   ├── tmdbApi.ts        # Official TMDB API client & URL builders
    │   └── playbackService.ts# Decoupled authorized stream verification service
    ├── components/
    │   ├── Navbar.tsx        # Responsive navigation with search
    │   ├── Footer.tsx        # TMDB legal notice & region indicators
    │   ├── HeroBanner.tsx    # Cinematic spotlight with streaming rights check
    │   ├── MediaCard.tsx     # Reusable poster card with badges
    │   └── MovieRow.tsx      # Smooth horizontal scroll row
    └── pages/
        ├── HomePage.tsx      # Curated rows (Telugu, Hindi, Tamil, TV, etc.)
        ├── MoviesPage.tsx    # Filter by Language, Genre, and Multi-page Pagination
        ├── TvShowsPage.tsx   # Popular & Trending Series with Pagination
        ├── SearchPage.tsx    # Live multi-search (Movies, TV Shows, People)
        ├── MovieDetailsPage.tsx # Complete TMDB metadata & India watch providers
        ├── TvShowDetailsPage.tsx # Seasons guide & live episode list
        ├── WatchPage.tsx     # Authorized HTML5 player / Legal availability notice
        ├── AdminPage.tsx     # CMS to associate authorized streams with TMDB IDs
        └── MyListPage.tsx    # Saved watchlist
```

---

## 🛠️ Local Development & Testing

```bash
# Install dependencies
npm install

# Run development server
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview
```

---

## ⚖️ Decoupled Playback Architecture & Legal Compliance

1. **Metadata**: TMDB API is used purely for catalog metadata (synopses, release dates, posters, star cast, runtime, and regional India legal watch providers).
2. **Playback Rights**: Commercial full-length streaming is never faked or scraped from third-party OTT portals.
3. If an authorized playback source is configured in the Admin CMS, the HTML5 video player is enabled.
4. If no authorized source exists, the application displays:
   > *"Streaming source is not available."*
   > *"Available information only — streaming unavailable in this app."*
