import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  output: 'standalone',
  env: {
    NEXT_PUBLIC_API_DEV_BASE_URL_V1: process.env.NEXT_PUBLIC_API_DEV_BASE_URL_V1,
    NEXT_PUBLIC_API_PROD_BASE_URL_V1: process.env.NEXT_PUBLIC_API_PROD_BASE_URL_V1,
  },
};

export default nextConfig;
