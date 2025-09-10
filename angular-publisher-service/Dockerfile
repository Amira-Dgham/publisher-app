# Multi-stage Dockerfile for Angular application

# Stage 1: Build stage
FROM node:22-alpine AS builder

# Set working directory
WORKDIR /app

# Copy package files first (better Docker layer caching)
COPY package*.json ./

# Install dependencies
RUN npm ci --only=production && npm cache clean --force

# Copy source code
COPY . .

# Build the Angular application
RUN npm run build:$NG_ENV 2>/dev/null || npm run build -- --configuration=$NG_ENV

# Stage 3: Development stage (for hot reload)
FROM node:22-alpine AS development

WORKDIR /app

# Copy package files
COPY package*.json ./

# Install all dependencies (including dev dependencies)
RUN npm ci

# Copy source code
COPY . .

# Build arguments for development
ARG NG_ENV=dev
ARG NODE_ENV=development

# Set environment variables
ENV NODE_ENV=$NODE_ENV
ENV NG_ENV=$NG_ENV

# Expose port
EXPOSE 4200

# Start development server with hot reload
CMD ["npm", "start"]