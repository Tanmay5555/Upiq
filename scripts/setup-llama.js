import http from 'http';
import { URL } from 'url';

const OLLAMA_BASE_URL = process.env.OLLAMA_BASE_URL || 'http://localhost:11434';
const OLLAMA_MODEL = process.env.OLLAMA_MODEL || 'llama3';

console.log('====================================================');
console.log('🦙 UPIQ AI - Llama / Ollama AI Connection Setup');
console.log('====================================================\n');
console.log(`🌐 Base URL Target: ${OLLAMA_BASE_URL}`);
console.log(`🤖 Configured Llama Model: ${OLLAMA_MODEL}\n`);

function makeRequest(urlStr, method = 'GET', bodyData = null) {
  return new Promise((resolve, reject) => {
    const parsed = new URL(urlStr);
    const options = {
      hostname: parsed.hostname,
      port: parsed.port || 80,
      path: parsed.pathname + parsed.search,
      method: method,
      headers: {
        'Content-Type': 'application/json'
      },
      timeout: 5000
    };

    const req = http.request(options, (res) => {
      let data = '';
      res.on('data', (chunk) => { data += chunk; });
      res.on('end', () => {
        try {
          const json = JSON.parse(data);
          resolve({ status: res.statusCode, body: json });
        } catch (e) {
          resolve({ status: res.statusCode, body: data });
        }
      });
    });

    req.on('error', (err) => reject(err));
    req.on('timeout', () => {
      req.destroy();
      reject(new Error('Connection timed out'));
    });

    if (bodyData) {
      req.write(JSON.stringify(bodyData));
    }
    req.end();
  });
}

async function verifyLlamaConnection() {
  console.log('📡 Step 1: Checking Ollama service availability...');
  try {
    const res = await makeRequest(`${OLLAMA_BASE_URL}/api/tags`);
    if (res.status === 200) {
      console.log('  ✅ Ollama service is running and accessible!\n');
      
      console.log('🔍 Step 2: Verifying installed Llama models...');
      const models = Array.isArray(res.body?.models) ? res.body.models : [];
      console.log(`  Found ${models.length} installed model(s):`);
      
      let targetFound = false;
      models.forEach(m => {
        const name = m.name || m.model;
        console.log(`  - 📦 ${name}`);
        if (name.startsWith(OLLAMA_MODEL)) {
          targetFound = true;
        }
      });

      if (targetFound) {
        console.log(`\n  ✅ Configured model '${OLLAMA_MODEL}' is ready!\n`);
        
        console.log('🧪 Step 3: Running test inference generation...');
        try {
          const testGen = await makeRequest(`${OLLAMA_BASE_URL}/api/generate`, 'POST', {
            model: OLLAMA_MODEL,
            prompt: 'Reply with "UPIQ AI Llama connection successful" if you can read this.',
            stream: false
          });
          if (testGen.status === 200 && testGen.body?.response) {
            console.log('  ✅ Inference test succeeded!');
            console.log(`  💬 Response: "${testGen.body.response.trim()}"`);
          }
        } catch (genErr) {
          console.log(`  ⚠️  Inference test note: ${genErr.message}`);
        }
      } else {
        console.log(`\n  ⚠️ Model '${OLLAMA_MODEL}' not found locally in Ollama.`);
        console.log(`  💡 Run this command in your terminal to pull the model:`);
        console.log(`     ollama pull ${OLLAMA_MODEL}\n`);
      }
    } else {
      console.log(`  ⚠️  Ollama responded with status code ${res.status}`);
    }
  } catch (err) {
    console.log(`  ⚠️  Could not connect to Ollama at ${OLLAMA_BASE_URL} (${err.message}).\n`);
    console.log('💡 HOW TO CONNECT LLAMA FOR THIS PROJECT:');
    console.log('----------------------------------------------------');
    console.log('1. Download & Install Ollama from https://ollama.com');
    console.log('2. Start Ollama and pull Llama 3:');
    console.log(`   ollama pull ${OLLAMA_MODEL}`);
    console.log('3. Ensure Ollama is running at http://localhost:11434');
    console.log('4. Verify environment variables in .env:');
    console.log('   OLLAMA_BASE_URL=http://localhost:11434');
    console.log(`   OLLAMA_MODEL=${OLLAMA_MODEL}`);
    console.log('----------------------------------------------------\n');
  }

  console.log('====================================================');
  console.log('✨ LLAMA CONNECTION VERIFICATION COMPLETE');
  console.log('====================================================\n');
}

verifyLlamaConnection();
