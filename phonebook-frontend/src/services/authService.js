import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json'
  }
})

export const login = (username, password) => {
  return api.post('/auth/login', {
    username,
    password
  })
}