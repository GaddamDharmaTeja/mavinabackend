import { Db, MongoClient } from "mongodb";
let client: MongoClient | undefined;
let database: Db | undefined;
export async function db() {
  if (database) return database;
  const uri = process.env.MONGODB_URI;
  if (!uri) throw new Error("MONGODB_URI is not configured");
  const pendingClient = new MongoClient(uri, { serverSelectionTimeoutMS: 10_000, connectTimeoutMS: 10_000 });
  try {
    await pendingClient.connect();
    const connectedDatabase = pendingClient.db();
    await seed(connectedDatabase);
    client = pendingClient;
    database = connectedDatabase;
    return database;
  } catch (error) {
    await pendingClient.close().catch(() => undefined);
    throw error;
  }
}
async function seed(store: Db) {
  if (await store.collection("products").countDocuments()) return;
  await store.collection("products").insertMany([
    { name: "Banganapalli Mango", variety: "Banganapalli", price: 799, weight: "3 kg box", description: "Golden, fragrant and naturally sweet.", featured: true, active: true, stockQuantity: 24, imageUrl: "", createdAt: new Date() },
    { name: "Kesar Mango", variety: "Kesar", price: 899, weight: "3 kg box", description: "Saffron-coloured pulp with a rich aroma.", featured: true, active: true, stockQuantity: 18, imageUrl: "", createdAt: new Date() },
    { name: "Alphonso Mango", variety: "Alphonso", price: 1099, weight: "3 kg box", description: "The celebrated king of mangoes, harvested with care.", featured: true, active: true, stockQuantity: 12, imageUrl: "", createdAt: new Date() }
  ]);
  await store.collection("categories").insertMany(["Banganapalli", "Kesar", "Alphonso"].map(name => ({ name, active: true })));
}
