<template>
  <div class="app-shell">
    <header class="topbar">
      <div class="brand">
        <div class="brand-mark">PB</div>
        <div>
          <h1>Phonebook</h1>
          <p>Simple contact management</p>
        </div>
      </div>
      <button class="add-btn" @click="openCreate">
        <span>+</span> Add Contact
      </button>
    </header>

    <main class="content">
      <section class="hero">
        <div>
          <p class="eyebrow">CONTACT DIRECTORY</p>
          <h2>All your contacts, <span>organized.</span></h2>
          <p class="hero-copy">Search, view, edit and manage your contacts from one clean workspace.</p>
        </div>
        <div class="hero-decoration">
          <div class="bubble bubble-one"></div>
          <div class="bubble bubble-two"></div>
          <div class="hero-icon">☎</div>
        </div>
      </section>

      <section class="stats">
        <div class="stat-card">
          <div class="stat-icon blue">◎</div>
          <div><small>Total Contacts</small><strong>{{ totalElements }}</strong></div>
        </div>
        <div class="stat-card">
          <div class="stat-icon green">✓</div>
          <div><small>Current Page</small><strong>{{ currentPage + 1 }} / {{ totalPages || 1 }}</strong></div>
        </div>
        <div class="stat-card">
          <div class="stat-icon purple">⌕</div>
          <div><small>Showing</small><strong>{{ contacts.length }} Results</strong></div>
        </div>
      </section>

      <section class="directory">
        <div class="directory-head">
          <div>
            <h3>Contact Directory</h3>
            <p>{{ query ? `Results for "${query}"` : 'Browse your saved contacts' }}</p>
          </div>
          <div class="page-size">
            <span>Show</span>
            <select v-model.number="pageSize" @change="changePageSize">
              <option :value="6">6</option>
              <option :value="9">9</option>
              <option :value="12">12</option>
              <option :value="24">24</option>
            </select>
          </div>
        </div>

        <div class="search-row">
          <div class="search-box">
            <span>⌕</span>
            <input
              v-model="query"
              type="search"
              placeholder="Search by name or phone number..."
              @input="scheduleSearch"
            />
            <button v-if="query" class="clear-search" @click="clearSearch">×</button>
          </div>
          <button class="refresh-btn" @click="loadContacts">↻ Refresh</button>
        </div>

        <div v-if="message" :class="['toast', messageType]">
          <span>{{ message }}</span>
          <button @click="message = ''">×</button>
        </div>

        <div v-if="loading" class="loading">
          <div class="spinner"></div>
          <p>Loading contacts...</p>
        </div>

        <div v-else-if="contacts.length === 0" class="empty">
          <div class="empty-icon">⌕</div>
          <h3>{{ query ? 'No matching contacts' : 'Your directory is empty' }}</h3>
          <p>{{ query ? 'Try another name or phone number.' : 'Add your first contact to get started.' }}</p>
          <button v-if="!query" class="add-btn small" @click="openCreate">+ Add Contact</button>
        </div>

        <div v-else class="contact-grid">
          <article v-for="contact in contacts" :key="contact.id" class="contact-card">
            <div class="card-top">
              <div class="avatar" :style="{ background: avatarColor(contact.name) }">
                {{ initials(contact.name) }}
              </div>
              <div class="card-menu">
                <button @click="openEdit(contact)" title="Edit">✎</button>
              </div>
            </div>

            <h4>{{ contact.name }}</h4>
            <div class="contact-line">
              <span class="line-icon">☎</span>
              <span>{{ contact.phoneNumber }}</span>
            </div>
            <div v-if="contact.email" class="contact-line">
              <span class="line-icon">✉</span>
              <span class="truncate">{{ contact.email }}</span>
            </div>
            <div v-if="contact.address" class="contact-line">
              <span class="line-icon">⌖</span>
              <span class="truncate">{{ contact.address }}</span>
            </div>

            <div class="card-footer">
              <span class="created">Added {{ formatDate(contact.createdAt) }}</span>
              <div class="actions">
                <button class="view" @click="viewContact(contact.id)">View</button>
                <button class="delete" @click="removeContact(contact.id)">Delete</button>
              </div>
            </div>
          </article>
        </div>

        <div v-if="!loading && totalElements > 0" class="pagination">
          <div class="pagination-info">
            Showing <strong>{{ startItem }}</strong>–<strong>{{ endItem }}</strong> of <strong>{{ totalElements }}</strong>
          </div>

          <div class="pagination-controls">
            <button :disabled="currentPage === 0" @click="goToPage(currentPage - 1)">‹</button>

            <button
              v-for="page in visiblePages"
              :key="page"
              :class="{ active: page === currentPage }"
              @click="goToPage(page)"
            >
              {{ page + 1 }}
            </button>

            <button :disabled="currentPage >= totalPages - 1" @click="goToPage(currentPage + 1)">›</button>
          </div>
        </div>
      </section>
    </main>

    <!-- Add / Edit modal -->
    <div v-if="showForm" class="modal-backdrop" @click.self="closeForm">
      <div class="modal">
        <div class="modal-head">
          <div>
            <span class="modal-eyebrow">{{ editingId ? 'UPDATE CONTACT' : 'NEW CONTACT' }}</span>
            <h2>{{ editingId ? 'Edit contact' : 'Add a contact' }}</h2>
            <p>Keep the information clear and up to date.</p>
          </div>
          <button class="close-btn" @click="closeForm">×</button>
        </div>

        <form @submit.prevent="saveContact">
          <div class="field">
            <label>Name <b>*</b></label>
            <input v-model="form.name" maxlength="255" placeholder="Full name" />
            <small v-if="errors.name">{{ errors.name }}</small>
          </div>

          <div class="field">
            <label>Phone Number <b>*</b></label>
            <input v-model="form.phoneNumber" maxlength="30" placeholder="e.g. 9876543210" />
            <small v-if="errors.phone">{{ errors.phone }}</small>
          </div>

          <div class="two-col">
            <div class="field">
              <label>Email</label>
              <input v-model="form.email" type="email" maxlength="255" placeholder="name@example.com" />
              <small v-if="errors.email">{{ errors.email }}</small>
            </div>
            <div class="field">
              <label>Address</label>
              <input v-model="form.address" placeholder="City, State" />
            </div>
          </div>

          <div class="modal-actions">
            <button type="button" class="cancel-btn" @click="closeForm">Cancel</button>
            <button type="submit" class="save-btn" :disabled="saving">
              {{ saving ? 'Saving...' : (editingId ? 'Save Changes' : 'Create Contact') }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- View modal -->
    <div v-if="selected" class="modal-backdrop" @click.self="selected = null">
      <div class="modal details-modal">
        <div class="modal-head">
          <div>
            <span class="modal-eyebrow">CONTACT DETAILS</span>
            <h2>{{ selected.name }}</h2>
            <p>Contact #{{ selected.id }}</p>
          </div>
          <button class="close-btn" @click="selected = null">×</button>
        </div>
        <div class="detail-list">
          <div><span>Phone</span><strong>{{ selected.phoneNumber }}</strong></div>
          <div><span>Email</span><strong>{{ selected.email || 'Not provided' }}</strong></div>
          <div><span>Address</span><strong>{{ selected.address || 'Not provided' }}</strong></div>
          <div><span>Created</span><strong>{{ formatDateTime(selected.createdAt) }}</strong></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, reactive, ref } from 'vue'
import {
  searchContacts,
  getContactById,
  createContact,
  updateContact,
  deleteContact
} from './services/contactService'

const contacts = ref([])
const query = ref('')
const currentPage = ref(0)
const pageSize = ref(6)
const totalElements = ref(0)
const totalPages = ref(0)
const loading = ref(false)
const saving = ref(false)
const showForm = ref(false)
const editingId = ref(null)
const selected = ref(null)
const message = ref('')
const messageType = ref('success')
let searchTimer = null

const form = reactive({ name: '', phoneNumber: '', email: '', address: '' })
const errors = reactive({ name: '', phone: '', email: '' })

const startItem = computed(() =>
  totalElements.value === 0 ? 0 : currentPage.value * pageSize.value + 1
)

const endItem = computed(() =>
  Math.min((currentPage.value + 1) * pageSize.value, totalElements.value)
)

const visiblePages = computed(() => {
  const total = totalPages.value
  const current = currentPage.value
  if (total <= 5) return Array.from({ length: total }, (_, i) => i)
  let start = Math.max(0, current - 2)
  let end = Math.min(total, start + 5)
  if (end - start < 5) start = end - 5
  return Array.from({ length: end - start }, (_, i) => start + i)
})

function setMessage(text, type = 'success') {
  message.value = text
  messageType.value = type
}

async function loadContacts() {
  loading.value = true
  try {
    const response = await searchContacts(query.value, currentPage.value, pageSize.value)
    contacts.value = response.data.content
    totalElements.value = response.data.totalElements
    totalPages.value = response.data.totalPages
    if (currentPage.value >= totalPages.value && totalPages.value > 0) {
      currentPage.value = totalPages.value - 1
      return loadContacts()
    }
  } catch (error) {
    setMessage(errorText(error), 'error')
  } finally {
    loading.value = false
  }
}

function scheduleSearch() {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => {
    currentPage.value = 0
    loadContacts()
  }, 300)
}

function clearSearch() {
  query.value = ''
  currentPage.value = 0
  loadContacts()
}

function changePageSize() {
  currentPage.value = 0
  loadContacts()
}

function goToPage(page) {
  if (page < 0 || page >= totalPages.value || page === currentPage.value) return
  currentPage.value = page
  loadContacts()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function resetForm() {
  form.name = ''; form.phoneNumber = ''; form.email = ''; form.address = ''
  errors.name = ''; errors.phone = ''; errors.email = ''
}

function openCreate() {
  resetForm()
  editingId.value = null
  showForm.value = true
}

function openEdit(contact) {
  errors.name = ''; errors.phone = ''; errors.email = ''
  editingId.value = contact.id
  form.name = contact.name
  form.phoneNumber = contact.phoneNumber
  form.email = contact.email || ''
  form.address = contact.address || ''
  showForm.value = true
}

function closeForm() {
  showForm.value = false
  editingId.value = null
  resetForm()
}

function validate() {
  errors.name = ''; errors.phone = ''; errors.email = ''
  let valid = true

  if (!form.name.trim()) {
    errors.name = 'Name is required.'
    valid = false
  }

  const phonePattern = /^[6-9][0-9]{9}$/
  if (!form.phoneNumber.trim()) {
    errors.phone = 'Phone number is required.'
    valid = false
  } else if (!phonePattern.test(form.phoneNumber.trim())) {
    errors.phone = 'Enter a valid phone number.'
    valid = false
  }

  if (form.email.trim() && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email.trim())) {
    errors.email = 'Enter a valid email address.'
    valid = false
  }

  return valid
}

async function saveContact() {
  message.value = ''
  if (!validate()) return

  saving.value = true
  const payload = {
    name: form.name.trim(),
    phoneNumber: form.phoneNumber.trim(),
    email: form.email.trim() || null,
    address: form.address.trim() || null
  }

  try {
    if (editingId.value) {
      await updateContact(editingId.value, payload)
      setMessage('Contact updated successfully.')
    } else {
      await createContact(payload)
      setMessage('Contact added successfully.')
    }
    closeForm()
    currentPage.value = 0
    await loadContacts()
  } catch (error) {
    setMessage(errorText(error), 'error')
  } finally {
    saving.value = false
  }
}

async function viewContact(id) {
  try {
    selected.value = (await getContactById(id)).data
  } catch (error) {
    setMessage(errorText(error), 'error')
  }
}

async function removeContact(id) {
  if (!window.confirm('Delete this contact?')) return

  try {
    await deleteContact(id)
    setMessage('Contact deleted successfully.')
    await loadContacts()
  } catch (error) {
    setMessage(errorText(error), 'error')
  }
}

function initials(name) {
  return name.split(/\s+/).filter(Boolean).slice(0, 2).map(x => x[0].toUpperCase()).join('')
}

function avatarColor(name) {
  const colors = ['#3157d5', '#7c4dff', '#0f8f72', '#d46a3a', '#bd4f85', '#4361a8']
  const sum = [...name].reduce((a, c) => a + c.charCodeAt(0), 0)
  return colors[sum % colors.length]
}

function formatDate(value) {
  if (!value) return '—'
  const date = new Date(value)
  return Number.isNaN(date.getTime()) ? value : date.toLocaleDateString()
}

function formatDateTime(value) {
  if (!value) return '—'
  const date = new Date(value)
  return Number.isNaN(date.getTime()) ? value : date.toLocaleString()
}

function errorText(error) {
  if (error.response?.data?.message) return error.response.data.message
  if (error.response?.status === 409) return 'Phone number or email already exists.'
  if (error.response?.status === 404) return 'Contact not found.'
  if (error.code === 'ERR_NETWORK') return 'Cannot connect to backend. Make sure Spring Boot is running on port 8080.'
  return 'Something went wrong. Please try again.'
}

onMounted(loadContacts)
onUnmounted(() => clearTimeout(searchTimer))
</script>
