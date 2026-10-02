import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080',
  headers: { 'Content-Type': 'application/json' }
})

export const searchContacts = (query, page, size) =>
  api.get('/contacts/search', { params: { query, page, size } })

export const getContactById = (id) => api.get(`/contacts/${id}`)
export const createContact = (contact) => api.post('/contacts', contact)
export const updateContact = (id, contact) => api.put(`/contacts/${id}`, contact)
export const deleteContact = (id) => api.delete(`/contacts/${id}`)
