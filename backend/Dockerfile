# Stage 1: Build
FROM node:20-alpine AS builder

WORKDIR /app

COPY package*.json ./
COPY prisma ./prisma/

RUN npm ci

COPY . .

RUN npx prisma generate
RUN npm run build

# Stage 2: Production
FROM node:20-alpine

WORKDIR /app

RUN apk add --no-cache openssl

COPY package*.json ./
RUN npm ci --only=production

COPY --from=builder /app/dist ./dist
COPY --from=builder /app/prisma ./prisma
COPY --from=builder /app/node_modules/.prisma ./node_modules/.prisma

# Create uploads directory and copy initial product photos from builder
RUN mkdir -p /app/uploads
COPY --from=builder /app/uploads ./uploads

ENV NODE_ENV=production
EXPOSE 3000

# Run Prisma migration deploy on startup, then start production server
CMD npx prisma migrate deploy && node dist/src/main.js
