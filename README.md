# Maviina Mane API

Set `MONGODB_URI` to your MongoDB connection string, then run `mvn spring-boot:run` from this directory. On first start the API creates and seeds `products` and `categories` collections. It exposes `GET /api/products` (with optional `variety` and `maxPrice`) and `GET /api/categories`.

For Razorpay, set `RAZORPAY_KEY_ID` and `RAZORPAY_KEY_SECRET` as environment variables before starting the API. Copy `.env.example` for the variable names; do not commit real credentials.
