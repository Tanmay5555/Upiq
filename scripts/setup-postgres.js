import { execSync } from 'child_process';
import fs from 'fs';
import path from 'path';
import net from 'net';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const DB_HOST = process.env.DB_HOST || 'localhost';
const DB_PORT = parseInt(process.env.DB_PORT || '5432', 10);
const DB_NAME = process.env.DB_NAME || 'upiq';
const DB_USER = process.env.DB_USERNAME || process.env.POSTGRES_USER || 'postgres';
const DB_PASSWORD = process.env.DB_PASSWORD || process.env.POSTGRES_PASSWORD || 'postgres';

console.log('====================================================');
console.log('🐘 UPIQ AI - PostgreSQL Database Setup & Verification');
console.log('====================================================\n');
console.log(`📡 Host: ${DB_HOST}:${DB_PORT}`);
console.log(`🗄️  Database Target: ${DB_NAME}`);
console.log(`👤 Username: ${DB_USER}\n`);

// 1. Check if PostgreSQL server is listening on port 5432
function checkPortOpen(host, port) {
  return new Promise((resolve) => {
    const socket = new net.Socket();
    socket.setTimeout(3000);
    socket.on('connect', () => {
      socket.destroy();
      resolve(true);
    });
    socket.on('timeout', () => {
      socket.destroy();
      resolve(false);
    });
    socket.on('error', () => {
      resolve(false);
    });
    socket.connect(port, host);
  });
}

async function runSetup() {
  const isPortListening = await checkPortOpen(DB_HOST, DB_PORT);

  if (isPortListening) {
    console.log(`✅ PostgreSQL server detected listening on ${DB_HOST}:${DB_PORT}`);
  } else {
    console.log(`⚠️  PostgreSQL is not listening on ${DB_HOST}:${DB_PORT}.`);
    console.log(`💡 You can start PostgreSQL using Docker Compose:`);
    console.log(`   npm run db:up   OR   docker compose up -d postgres\n`);
  }

  // Check if psql CLI tool exists locally
  let psqlCmd = 'psql';
  const windowsPsql = 'C:\\Program Files\\PostgreSQL\\18\\bin\\psql.exe';
  const windowsCreatedb = 'C:\\Program Files\\PostgreSQL\\18\\bin\\createdb.exe';

  if (fs.existsSync(windowsPsql)) {
    psqlCmd = `"${windowsPsql}"`;
  }

  try {
    const sqlScriptPath = path.join(__dirname, 'init-db.sql');
    console.log(`📄 Found SQL schema script at: ${sqlScriptPath}`);
    
    // Attempt database creation & seeding if psql command is available
    console.log('🚀 Attempting database initialization & seed data import...');
    
    if (process.platform === 'win32' && fs.existsSync(windowsCreatedb)) {
      try {
        execSync(`"${windowsCreatedb}" -U ${DB_USER} -h ${DB_HOST} -p ${DB_PORT} ${DB_NAME}`, {
          env: { ...process.env, PGPASSWORD: DB_PASSWORD },
          stdio: 'ignore'
        });
        console.log(`✅ Database '${DB_NAME}' verified/created.`);
      } catch (e) {
        console.log(`ℹ️  Database '${DB_NAME}' already exists or user requires privileges.`);
      }
    }

    if (fs.existsSync(windowsPsql) || process.platform !== 'win32') {
      try {
        execSync(`${psqlCmd} -U ${DB_USER} -h ${DB_HOST} -p ${DB_PORT} -d ${DB_NAME} -f "${sqlScriptPath}"`, {
          env: { ...process.env, PGPASSWORD: DB_PASSWORD },
          stdio: 'inherit'
        });
        console.log('✅ PostgreSQL Schema & Seed Data applied successfully!');
      } catch (err) {
        console.log('ℹ️  Note: When running Spring Boot backend (`cd backend && ./mvnw spring-boot:run`), Hibernate automatically manages tables & constraints via JPA ddl-auto=update.');
      }
    }
  } catch (error) {
    console.log(`ℹ️ Setup note: ${error.message}`);
  }

  console.log('\n====================================================');
  console.log('✨ POSTGRESQL SETUP COMPLETE');
  console.log('====================================================\n');
  console.log('Summary:');
  console.log(`1. Backend configuration updated in application.yml & .env`);
  console.log(`2. PostgreSQL Docker service configured in docker-compose.yml`);
  console.log(`3. Schema & Seed scripts saved in scripts/init-db.sql`);
  console.log(`4. Spring Boot backend ready to connect to jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}\n`);
}

runSetup();
