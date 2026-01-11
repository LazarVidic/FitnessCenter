# ===== BUILD =====
FROM node:20-bookworm-slim AS build
WORKDIR /app

COPY package.json package-lock.json* ./
RUN npm ci || npm install

COPY . .
RUN npm run build

# ===== RUN (NGINX) =====
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html

# (Opcionalno) SPA fallback za React Router
RUN rm /etc/nginx/conf.d/default.conf
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
