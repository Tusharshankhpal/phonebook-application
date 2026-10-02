<template>
  <div class="login-page">

    <div class="login-background">
      <div class="login-orb login-orb-one"></div>
      <div class="login-orb login-orb-two"></div>
      <div class="login-grid"></div>
    </div>

    <div class="login-layout">

      <section class="login-brand">

        <div class="login-brand-logo">

          <div class="login-brand-mark">
            PB
          </div>

          <span>
            Phonebook
          </span>

        </div>

        <div class="login-brand-content">

          <span class="login-eyebrow">
            CONTACT MANAGEMENT
          </span>

          <h1>
            Your contacts,
            <span> beautifully organized.</span>
          </h1>

          <p>
            Manage your contacts, search instantly and keep
            your directory organized from one simple workspace.
          </p>

        </div>

        <div class="login-brand-footer">
          <span>Secure workspace</span>
          <span>•</span>
          <span>Admin access</span>
        </div>

      </section>


      <section class="login-section">

        <div class="login-card">

          <div class="login-header">

            <div class="login-welcome-icon">
              →
            </div>

            <span class="login-card-eyebrow">
              WELCOME BACK
            </span>

            <h2>
              Sign in
            </h2>

            <p>
              Enter your credentials to access your phonebook.
            </p>

          </div>


          <form @submit.prevent="handleLogin">

            <div class="login-field">

              <label>
                Username
              </label>

              <div class="login-input-wrapper">

                <span class="login-input-icon">
                  ◎
                </span>

                <input
                  v-model="username"
                  type="text"
                  placeholder="Enter username"
                  autocomplete="username"
                  :disabled="loading"
                />

              </div>

            </div>


            <div class="login-field">

              <label>
                Password
              </label>

              <div class="login-input-wrapper">

                <span class="login-input-icon">
                  ⌑
                </span>

                <input
                  v-model="password"
                  :type="showPassword ? 'text' : 'password'"
                  placeholder="Enter password"
                  autocomplete="current-password"
                  :disabled="loading"
                />

                <button
                  type="button"
                  class="password-toggle"
                  @click="showPassword = !showPassword"
                >
                  {{ showPassword ? 'Hide' : 'Show' }}
                </button>

              </div>

            </div>


            <div
              v-if="error"
              class="login-error"
            >
              <span>!</span>
              {{ error }}
            </div>


            <button
              type="submit"
              class="login-button"
              :disabled="loading"
            >
              {{ loading ? 'Signing in...' : 'Sign in →' }}
            </button>

          </form>


          <div class="login-footer-text">
            Phonebook Admin Portal
          </div>

        </div>

      </section>

    </div>

  </div>
</template>


<script setup>

import { ref } from 'vue'

import { login } from './services/authService'

const emit = defineEmits([
  'login-success'
])

const username = ref('')
const password = ref('')

const error = ref('')
const loading = ref(false)

const showPassword = ref(false)


async function handleLogin() {

  error.value = ''

  if (!username.value.trim()) {
    error.value =
      'Please enter your username.'
    return
  }

  if (!password.value) {
    error.value =
      'Please enter your password.'
    return
  }

  loading.value = true

  try {

    const response =
      await login(
        username.value.trim(),
        password.value
      )

    if (response.data.success) {

      localStorage.setItem(
        'phonebook_logged_in',
        'true'
      )

      localStorage.setItem(
        'phonebook_username',
        response.data.username
      )

      emit('login-success')
    }

  } catch (err) {

    if (err.response?.status === 401) {

      error.value =
        'Invalid username or password.'

    } else {

      error.value =
        'Unable to connect to the server. Please try again.'

    }

  } finally {

    loading.value = false

  }
}

</script>