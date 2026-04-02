/** @type {import('next').NextConfig} */
const nextConfig = {
  async rewrites() {
    return [
      {
        source: '/api/:path*',
        destination: process.env.NODE_ENV === 'production' 
          ? 'http://backend:8080/api/:path*'
          : 'http://localhost:8080/api/:path*',
      },
    ];
  },
};

export default nextConfig;