# FROM ghcr.io/pnpm/pnpm:latest AS builder
# RUN pnpm runtime set node 22 -g
# WORKDIR /app
# COPY package.json package-lock.json* pnpm-lock.yaml* yarn.lock* ./
# COPY src ./src
# COPY public ./public
# COPY *.js *.ts *.json *.yaml *.html .
# RUN pnpm install --frozen-lockfile --dangerously-allow-all-builds
# RUN pnpm run build

# FROM nginx:1.25-alpine AS runner
# RUN rm -rf /usr/share/nginx/html/*
# COPY --from=builder /app/dist /usr/share/nginx/html
# EXPOSE 80
# ENTRYPOINT ["nginx", "-g", "daemon off;"]

# FROM node:22 AS runner
# WORKDIR /app
# COPY --from=builder /app /app
# RUN pnpm run build
# EXPOSE 4173
# ENTRYPOINT ["pnpm", "run", "preview", "--host"]

FROM ghcr.io/pnpm/pnpm:latest AS runner
RUN pnpm runtime set node 22 -g
WORKDIR /app
COPY package.json package-lock.json* pnpm-lock.yaml* yarn.lock* ./
COPY src ./src
COPY public ./public
COPY *.js *.ts *.json *.yaml *.html .
RUN pnpm install --frozen-lockfile --dangerously-allow-all-builds
RUN pnpm run build
EXPOSE 4173
ENTRYPOINT ["pnpm", "run", "start"]