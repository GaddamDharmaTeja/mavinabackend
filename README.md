# Maviina Mane API

Set `MONGODB_URI` to your MongoDB Atlas connection string, then run `mvn spring-boot:run` from this directory. On first start the API creates and seeds `products` and `categories` collections. It exposes `GET /api/products` (with optional `variety` and `maxPrice`) and `GET /api/categories`.

## Render deployment

Add these Render environment variables (do not commit secrets):

```text
MONGODB_URI=mongodb+srv://<user>:<password>@<cluster>/<database>?retryWrites=true&w=majority
CORS_ORIGIN=https://mavinafrontend.netlify.app,https://*.netlify.app
JWT_SECRET=<long-random-secret>
```

Render supplies `PORT` automatically. Verify `https://<your-service>.onrender.com/api/products` before connecting the Netlify site.

For Razorpay, set `RAZORPAY_KEY_ID` and `RAZORPAY_KEY_SECRET` as environment variables before starting the API. Copy `.env.example` for the variable names; do not commit real credentials.
