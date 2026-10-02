<template>
  <!-- =========================
       LOGIN
  ========================== -->
  <Login
    v-if="!isLoggedIn"
    @login-success="handleLoginSuccess"
  />

  <!-- =========================
       MAIN APPLICATION
  ========================== -->
  <div v-else class="app-shell">

    <!-- =========================
         TOP BAR
    ========================== -->
    <header class="topbar">

      <div class="brand">
        <div class="brand-mark">
          PB
        </div>

        <div class="brand-text">
          <h1>Phonebook</h1>
          <p>Contact workspace</p>
        </div>
      </div>


      <div class="topbar-actions">

        <div class="user-pill">
          <span class="user-avatar">
            {{ initials(username) }}
          </span>

          <span>
            {{ username }}
          </span>
        </div>


        <button
          class="add-btn"
          @click="openCreate"
        >
          <span class="add-icon">+</span>
          Add Contact
        </button>


        <button
          class="logout-btn"
          @click="logout"
        >
          Logout
        </button>

      </div>

    </header>


    <!-- =========================
         MAIN CONTENT
    ========================== -->
    <main class="content">

      <!-- =========================
           HERO
      ========================== -->
      <section class="hero">

        <div class="hero-content">

          <p class="eyebrow">
            CONTACT DIRECTORY
          </p>

          <h2>
            All your contacts,
            <span>organized.</span>
          </h2>

          <p class="hero-copy">
            Search, view, edit and manage your contacts
            from one clean workspace.
          </p>

        </div>


        <div class="hero-decoration">

          <div class="bubble bubble-one"></div>

          <div class="bubble bubble-two"></div>

          <div class="hero-icon">
            ☎
          </div>

        </div>

      </section>


      <!-- =========================
           STATISTICS
      ========================== -->
      <section class="stats">

        <!-- TOTAL -->
        <div class="stat-card">

          <div class="stat-icon blue">
            ◎
          </div>

          <div class="stat-content">

            <small>
              Total Contacts
            </small>

            <strong>
              {{ totalElements }}
            </strong>

          </div>

        </div>


        <!-- CURRENT PAGE -->
        <div class="stat-card">

          <div class="stat-icon green">
            ✓
          </div>

          <div class="stat-content">

            <small>
              Current Page
            </small>

            <strong>
              {{ totalPages ? currentPage + 1 : 0 }}
              /
              {{ totalPages }}
            </strong>

          </div>

        </div>


        <!-- SHOWING -->
        <div class="stat-card">

          <div class="stat-icon purple">
            ⌕
          </div>

          <div class="stat-content">

            <small>
              Showing
            </small>

            <strong>
              {{ contacts.length }}
              Results
            </strong>

          </div>

        </div>

      </section>


      <!-- =========================
           DIRECTORY
      ========================== -->
      <section class="directory">

        <!-- DIRECTORY HEADER -->

        <div class="directory-head">

          <div class="directory-title">

            <h3>
              Contact Directory
            </h3>

            <p>
              {{
                query
                  ? `Results for "${query}"`
                  : 'Browse your saved contacts'
              }}
            </p>

          </div>


          <div class="page-size">

            <span>
              Show
            </span>

            <select
              v-model.number="pageSize"
              @change="changePageSize"
            >

              <option :value="6">
                6
              </option>

              <option :value="12">
                12
              </option>

              <option :value="24">
                24
              </option>

              <option :value="48">
                48
              </option>

            </select>

          </div>

        </div>


        <!-- =========================
             SEARCH BAR
        ========================== -->
        <div class="search-row">

          <div class="search-box">

            <span class="search-icon">
              ⌕
            </span>

            <input
              v-model="query"
              type="search"
              placeholder="Search by name or phone number..."
              @input="scheduleSearch"
            />

            <button
              v-if="query"
              class="clear-search"
              @click="clearSearch"
              aria-label="Clear search"
            >
              ×
            </button>

          </div>


          <button
            class="refresh-btn"
            @click="loadContacts"
          >
            <span>↻</span>
            Refresh
          </button>

        </div>


        <!-- =========================
             MESSAGE
        ========================== -->
        <div
          v-if="message"
          :class="[
            'toast',
            messageType
          ]"
        >

          <span>
            {{ message }}
          </span>

          <button
            @click="message = ''"
            aria-label="Close message"
          >
            ×
          </button>

        </div>


        <!-- =========================
             LOADING
        ========================== -->
        <div
          v-if="loading"
          class="loading"
        >

          <div class="spinner"></div>

          <p>
            Loading contacts...
          </p>

        </div>


        <!-- =========================
             EMPTY STATE
        ========================== -->
        <div
          v-else-if="contacts.length === 0"
          class="empty"
        >

          <div class="empty-icon">
            ⌕
          </div>

          <h3>
            {{
              query
                ? 'No matching contacts'
                : 'Your directory is empty'
            }}
          </h3>

          <p>
            {{
              query
                ? 'Try another name or phone number.'
                : 'Add your first contact to get started.'
            }}
          </p>


          <button
            v-if="!query"
            class="add-btn small"
            @click="openCreate"
          >
            <span>+</span>
            Add Contact
          </button>

        </div>


        <!-- =========================
             CONTACT CARDS
        ========================== -->
        <div
          v-else
          class="contact-grid"
        >

          <article
            v-for="contact in contacts"
            :key="contact.id"
            class="contact-card"
          >

            <!-- CARD HEADER -->

            <div class="card-top">

              <div
                class="avatar"
                :style="{
                  background: avatarColor(contact.name)
                }"
              >
                {{ initials(contact.name) }}
              </div>


              <button
                class="edit-icon"
                title="Edit contact"
                @click="openEdit(contact)"
              >
                ✎
              </button>

            </div>


            <!-- NAME -->

            <h4>
              {{ contact.name }}
            </h4>


            <!-- PHONE -->

            <div class="contact-line">

              <span class="line-icon">
                ☎
              </span>

              <span>
                {{ contact.phoneNumber }}
              </span>

            </div>


            <!-- EMAIL -->

            <div
              v-if="contact.email"
              class="contact-line"
            >

              <span class="line-icon">
                ✉
              </span>

              <span class="truncate">
                {{ contact.email }}
              </span>

            </div>


            <!-- ADDRESS -->

            <div
              v-if="contact.address"
              class="contact-line"
            >

              <span class="line-icon">
                ⌖
              </span>

              <span class="truncate">
                {{ contact.address }}
              </span>

            </div>


            <!-- CARD FOOTER -->

            <div class="card-footer">

              <span class="created">
                Added {{ formatDate(contact.createdAt) }}
              </span>


              <div class="actions">

                <button
                  class="view"
                  @click="viewContact(contact.id)"
                >
                  View
                </button>


                <button
                  class="delete"
                  @click="removeContact(contact.id)"
                >
                  Delete
                </button>

              </div>

            </div>

          </article>

        </div>


        <!-- =========================
             PAGINATION
        ========================== -->
        <div
          v-if="!loading && totalElements > 0"
          class="pagination"
        >

          <div class="pagination-info">

            Showing

            <strong>
              {{ startItem }}
            </strong>

            –

            <strong>
              {{ endItem }}
            </strong>

            of

            <strong>
              {{ totalElements }}
            </strong>

          </div>


          <div class="pagination-controls">

            <!-- PREVIOUS -->

            <button
              :disabled="currentPage === 0"
              @click="goToPage(currentPage - 1)"
            >
              ‹
            </button>


            <!-- PAGE NUMBERS -->

            <button
              v-for="page in visiblePages"
              :key="page"
              :class="{
                active: page === currentPage
              }"
              @click="goToPage(page)"
            >
              {{ page + 1 }}
            </button>


            <!-- NEXT -->

            <button
              :disabled="
                currentPage >= totalPages - 1
              "
              @click="goToPage(currentPage + 1)"
            >
              ›
            </button>

          </div>

        </div>

      </section>

    </main>


    <!-- =====================================================
         ADD / EDIT CONTACT MODAL
    ====================================================== -->

    <div
      v-if="showForm"
      class="modal-backdrop"
      @click.self="closeForm"
    >

      <div class="modal">

        <!-- MODAL HEADER -->

        <div class="modal-head">

          <div>

            <span class="modal-eyebrow">

              {{
                editingId
                  ? 'UPDATE CONTACT'
                  : 'NEW CONTACT'
              }}

            </span>


            <h2>

              {{
                editingId
                  ? 'Edit contact'
                  : 'Add a contact'
              }}

            </h2>


            <p>
              Keep the information clear and up to date.
            </p>

          </div>


          <button
            class="close-btn"
            @click="closeForm"
          >
            ×
          </button>

        </div>


        <!-- FORM -->

        <form
          @submit.prevent="saveContact"
        >

          <!-- NAME -->

          <div class="field">

            <label>
              Name <b>*</b>
            </label>

            <input
              v-model="form.name"
              maxlength="255"
              placeholder="Full name"
            />

            <small
              v-if="errors.name"
            >
              {{ errors.name }}
            </small>

          </div>


          <!-- PHONE -->

          <div class="field">

            <label>
              Phone Number <b>*</b>
            </label>

            <input
              v-model="form.phoneNumber"
              maxlength="10"
              inputmode="numeric"
              placeholder="9876543210"
            />

            <small
              v-if="errors.phone"
            >
              {{ errors.phone }}
            </small>

          </div>


          <!-- EMAIL + ADDRESS -->

          <div class="two-col">

            <div class="field">

              <label>
                Email
              </label>

              <input
                v-model="form.email"
                type="email"
                maxlength="255"
                placeholder="name@example.com"
              />

              <small
                v-if="errors.email"
              >
                {{ errors.email }}
              </small>

            </div>


            <div class="field">

              <label>
                Address
              </label>

              <input
                v-model="form.address"
                placeholder="City, State"
              />

            </div>

          </div>


          <!-- ACTIONS -->

          <div class="modal-actions">

            <button
              type="button"
              class="cancel-btn"
              @click="closeForm"
            >
              Cancel
            </button>


            <button
              type="submit"
              class="save-btn"
              :disabled="saving"
            >

              {{
                saving
                  ? 'Saving...'
                  : (
                      editingId
                        ? 'Save Changes'
                        : 'Create Contact'
                    )
              }}

            </button>

          </div>

        </form>

      </div>

    </div>


    <!-- =====================================================
         VIEW CONTACT MODAL
    ====================================================== -->

    <div
      v-if="selected"
      class="modal-backdrop"
      @click.self="selected = null"
    >

      <div
        class="modal details-modal"
      >

        <!-- HEADER -->

        <div class="modal-head">

          <div>

            <span class="modal-eyebrow">
              CONTACT DETAILS
            </span>

            <h2>
              {{ selected.name }}
            </h2>

            <p>
              Contact #{{ selected.id }}
            </p>

          </div>


          <button
            class="close-btn"
            @click="selected = null"
          >
            ×
          </button>

        </div>


        <!-- DETAILS -->

        <div class="detail-list">

          <div>

            <span>
              Phone
            </span>

            <strong>
              {{ selected.phoneNumber }}
            </strong>

          </div>


          <div>

            <span>
              Email
            </span>

            <strong>
              {{ selected.email || 'Not provided' }}
            </strong>

          </div>


          <div>

            <span>
              Address
            </span>

            <strong>
              {{ selected.address || 'Not provided' }}
            </strong>

          </div>


          <div>

            <span>
              Created
            </span>

            <strong>
              {{ formatDateTime(selected.createdAt) }}
            </strong>

          </div>

        </div>

      </div>

    </div>

  </div>

</template>


<script setup>

import {
  computed,
  onMounted,
  onUnmounted,
  reactive,
  ref
} from 'vue'

import Login from './Login.vue'

import {
  searchContacts,
  getContactById,
  createContact,
  updateContact,
  deleteContact
} from './services/contactService'


/* =========================================================
   AUTHENTICATION
========================================================= */

const isLoggedIn = ref(
  localStorage.getItem(
    'phonebook_logged_in'
  ) === 'true'
)


const username = ref(
  localStorage.getItem(
    'phonebook_username'
  ) || 'admin'
)


function handleLoginSuccess() {

  username.value =
    localStorage.getItem(
      'phonebook_username'
    ) || 'admin'

  isLoggedIn.value = true

  loadContacts()
}


function logout() {

  localStorage.removeItem(
    'phonebook_logged_in'
  )

  localStorage.removeItem(
    'phonebook_username'
  )

  isLoggedIn.value = false

  contacts.value = []

  selected.value = null

  showForm.value = false
}


/* =========================================================
   CONTACT STATE
========================================================= */

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


/* =========================================================
   FORM STATE
========================================================= */

const form = reactive({
  name: '',
  phoneNumber: '',
  email: '',
  address: ''
})


const errors = reactive({
  name: '',
  phone: '',
  email: ''
})


/* =========================================================
   PAGINATION
========================================================= */

const startItem = computed(() => {

  if (totalElements.value === 0) {
    return 0
  }

  return (
    currentPage.value *
    pageSize.value
  ) + 1
})


const endItem = computed(() => {

  return Math.min(
    (
      currentPage.value + 1
    ) * pageSize.value,

    totalElements.value
  )
})


const visiblePages = computed(() => {

  const total = totalPages.value

  const current = currentPage.value


  if (total <= 5) {

    return Array.from(
      {
        length: total
      },
      (_, index) => index
    )

  }


  let start = Math.max(
    0,
    current - 2
  )


  let end = Math.min(
    total,
    start + 5
  )


  if (end - start < 5) {
    start = end - 5
  }


  return Array.from(
    {
      length: end - start
    },
    (_, index) =>
      start + index
  )

})


/* =========================================================
   MESSAGE
========================================================= */

function setMessage(
  text,
  type = 'success'
) {

  message.value = text

  messageType.value = type

}


/* =========================================================
   LOAD CONTACTS
========================================================= */

async function loadContacts() {

  if (!isLoggedIn.value) {
    return
  }


  loading.value = true


  try {

    const response =
      await searchContacts(
        query.value,
        currentPage.value,
        pageSize.value
      )


    contacts.value =
      response.data.content


    totalElements.value =
      response.data.totalElements


    totalPages.value =
      response.data.totalPages


    /*
     * If the current page no longer exists
     * after deleting/searching contacts,
     * move to the last available page.
     */

    if (
      currentPage.value >=
        totalPages.value &&
      totalPages.value > 0
    ) {

      currentPage.value =
        totalPages.value - 1

      return loadContacts()
    }


  } catch (error) {

    setMessage(
      errorText(error),
      'error'
    )

  } finally {

    loading.value = false

  }

}


/* =========================================================
   SEARCH
========================================================= */

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


/* =========================================================
   PAGE SIZE
========================================================= */

function changePageSize() {

  currentPage.value = 0

  loadContacts()

}


/* =========================================================
   PAGE NAVIGATION
========================================================= */

function goToPage(page) {

  if (
    page < 0 ||
    page >= totalPages.value ||
    page === currentPage.value
  ) {
    return
  }


  currentPage.value = page


  loadContacts()


  window.scrollTo({
    top: 0,
    behavior: 'smooth'
  })

}


/* =========================================================
   FORM RESET
========================================================= */

function resetForm() {

  form.name = ''

  form.phoneNumber = ''

  form.email = ''

  form.address = ''


  errors.name = ''

  errors.phone = ''

  errors.email = ''

}


/* =========================================================
   CREATE CONTACT
========================================================= */

function openCreate() {

  resetForm()

  editingId.value = null

  showForm.value = true

}


/* =========================================================
   EDIT CONTACT
========================================================= */

function openEdit(contact) {

  errors.name = ''

  errors.phone = ''

  errors.email = ''


  editingId.value =
    contact.id


  form.name =
    contact.name


  form.phoneNumber =
    contact.phoneNumber


  form.email =
    contact.email || ''


  form.address =
    contact.address || ''


  showForm.value = true

}


/* =========================================================
   CLOSE FORM
========================================================= */

function closeForm() {

  showForm.value = false

  editingId.value = null

  resetForm()

}


/* =========================================================
   VALIDATION
========================================================= */

function validate() {

  errors.name = ''

  errors.phone = ''

  errors.email = ''


  let valid = true


  /* NAME */

  if (!form.name.trim()) {

    errors.name =
      'Name is required.'

    valid = false

  }


  /* PHONE */

  const phonePattern =
    /^[6-9][0-9]{9}$/


  if (!form.phoneNumber.trim()) {

    errors.phone =
      'Phone number is required.'

    valid = false

  } else if (
    !phonePattern.test(
      form.phoneNumber.trim()
    )
  ) {

    errors.phone =
      'Enter a valid 10-digit Indian mobile number.'

    valid = false

  }


  /* EMAIL */

  if (
    form.email.trim() &&
    !/^[^\s@]+@[^\s@]+\.[^\s@]+$/
      .test(
        form.email.trim()
      )
  ) {

    errors.email =
      'Enter a valid email address.'

    valid = false

  }


  return valid

}


/* =========================================================
   SAVE CONTACT
========================================================= */

async function saveContact() {

  message.value = ''


  if (!validate()) {
    return
  }


  saving.value = true


  const payload = {

    name:
      form.name.trim(),

    phoneNumber:
      form.phoneNumber.trim(),

    email:
      form.email.trim() || null,

    address:
      form.address.trim() || null

  }


  try {

    /* UPDATE */

    if (editingId.value) {

      await updateContact(
        editingId.value,
        payload
      )


      setMessage(
        'Contact updated successfully.'
      )

    }

    /* CREATE */

    else {

      await createContact(
        payload
      )


      setMessage(
        'Contact added successfully.'
      )

    }


    closeForm()


    currentPage.value = 0


    await loadContacts()


  } catch (error) {

    setMessage(
      errorText(error),
      'error'
    )

  } finally {

    saving.value = false

  }

}


/* =========================================================
   VIEW CONTACT
========================================================= */

async function viewContact(id) {

  try {

    const response =
      await getContactById(id)


    selected.value =
      response.data


  } catch (error) {

    setMessage(
      errorText(error),
      'error'
    )

  }

}


/* =========================================================
   DELETE CONTACT
========================================================= */

async function removeContact(id) {

  const confirmed =
    window.confirm(
      'Delete this contact?'
    )


  if (!confirmed) {
    return
  }


  try {

    await deleteContact(id)


    setMessage(
      'Contact deleted successfully.'
    )


    await loadContacts()


  } catch (error) {

    setMessage(
      errorText(error),
      'error'
    )

  }

}


/* =========================================================
   UI HELPERS
========================================================= */

function initials(name) {

  if (!name) {
    return '?'
  }


  return name
    .split(/\s+/)
    .filter(Boolean)
    .slice(0, 2)
    .map(
      value =>
        value[0].toUpperCase()
    )
    .join('')

}


function avatarColor(name) {

  const colors = [
    '#3157d5',
    '#7c4dff',
    '#0f8f72',
    '#d46a3a',
    '#bd4f85',
    '#4361a8'
  ]


  if (!name) {
    return colors[0]
  }


  const sum =
    [...name].reduce(
      (
        total,
        character
      ) =>
        total +
        character.charCodeAt(0),
      0
    )


  return colors[
    sum % colors.length
  ]

}


function formatDate(value) {

  if (!value) {
    return '—'
  }


  const date =
    new Date(value)


  if (
    Number.isNaN(
      date.getTime()
    )
  ) {
    return value
  }


  return date.toLocaleDateString()

}


function formatDateTime(value) {

  if (!value) {
    return '—'
  }


  const date =
    new Date(value)


  if (
    Number.isNaN(
      date.getTime()
    )
  ) {
    return value
  }


  return date.toLocaleString()

}


/* =========================================================
   ERROR HANDLING
========================================================= */

function errorText(error) {

  if (
    error.response?.data?.message
  ) {

    return error.response.data.message

  }


  if (
    error.response?.status === 409
  ) {

    return 'Phone number or email already exists.'

  }


  if (
    error.response?.status === 404
  ) {

    return 'Contact not found.'

  }


  if (
    error.response?.status === 401
  ) {

    return 'Unauthorized request.'

  }


  if (
    error.code === 'ERR_NETWORK'
  ) {

    return 'Cannot connect to backend.'

  }


  return 'Something went wrong. Please try again.'

}


/* =========================================================
   LIFECYCLE
========================================================= */

onMounted(() => {

  if (isLoggedIn.value) {
    loadContacts()
  }

})


onUnmounted(() => {

  clearTimeout(searchTimer)

})

</script>