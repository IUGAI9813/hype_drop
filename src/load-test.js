import http from 'k6/http';
import { sleep } from 'k6';

export const options = {
  vus: 50,          
  duration: '5s',  
};

export default function () {

  const url = 'http://localhost:8080/api/v1/flash/1/purchase';
  const payload = JSON.stringify({ userId: `user_${Math.floor(Math.random() * 1000000)}` });
  const params = { headers: { 'Content-Type': 'application/json' } };

  http.post(url, payload, params);
  sleep(0.01);
}
