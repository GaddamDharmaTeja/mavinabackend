import express from "express";
import path from "node:path";
import cookieParser from "cookie-parser";
import router from "./routes";
import { attachSession } from "./lib/auth";
const app = express();
app.use(cookieParser());
app.use(express.json({ limit: "10mb" }));
app.use(attachSession);
// Preserve existing image URLs saved by the previous Spring Boot application.
app.use("/uploads", express.static(path.join(process.cwd(), "backend", "uploads")));
app.use("/api", router);
app.use((error: unknown, _req: express.Request, res: express.Response, _next: express.NextFunction) => {
  console.error(error);
  res.status(500).json({ error: "The service could not complete that request." });
});
export default app;
