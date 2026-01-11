FROM node:20-alpine
WORKDIR /app
COPY package.json package-lock.json ./
# Cài lại dependencies một lần nữa cho chắc chắn bên trong container chạy thật
RUN npm install
COPY . .
EXPOSE 5173
CMD ["npm", "run", "dev", "--", "--host"]