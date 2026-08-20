import crypto from "node:crypto";
import type { Request, Response, NextFunction } from "express";
const secret = () => process.env.SESSION_SECRET || process.env.JWT_SECRET || "development-only-change-me";
export type Role = "CUSTOMER" | "ADMIN" | "FARM_MANAGER";
export type Session = { id: string; role: Role; email: string; exp: number };
export function signSession(data: Omit<Session, "exp">) { const payload = Buffer.from(JSON.stringify({ ...data, exp: Date.now() + 604800000 })).toString("base64url"); return `${payload}.${crypto.createHmac("sha256", secret()).update(payload).digest("base64url")}`; }
export function readSession(raw?: string): Session | undefined { try { if (!raw) return; const [payload, signature] = raw.split("."); const expected = crypto.createHmac("sha256", secret()).update(payload).digest("base64url"); if (signature !== expected) return; const value = JSON.parse(Buffer.from(payload, "base64url").toString()) as Session; return value.exp > Date.now() ? value : undefined; } catch { return; } }
export function attachSession(req: Request, _res: Response, next: NextFunction) { (req as Request & { session?: Session }).session = readSession(req.cookies?.mm_session); next(); }
export function requireRole(...roles: Role[]) { return (req: Request, res: Response, next: NextFunction) => { const session = (req as Request & { session?: Session }).session; if (!session || !roles.includes(session.role)) return res.status(401).json({ error: "Authentication required" }); next(); }; }
export function setSession(res: Response, data: Omit<Session, "exp">) { res.cookie("mm_session", signSession(data), { httpOnly: true, secure: process.env.NODE_ENV === "production", sameSite: "lax", maxAge: 604800000, path: "/" }); }
export const hashPassword = (password: string) => crypto.scryptSync(password, "mavinamane", 64).toString("hex");
