import { useEffect, useId, useRef } from 'react'
import { GOOGLE_CLIENT_ID } from '@/lib/constants'

interface GoogleCredentialResponse {
  credential: string
}

declare global {
  interface Window {
    google?: {
      accounts: {
        id: {
          initialize: (config: {
            client_id: string
            callback: (response: GoogleCredentialResponse) => void
          }) => void
          renderButton: (parent: HTMLElement, options: Record<string, unknown>) => void
        }
      }
    }
  }
}

const SCRIPT_SRC = 'https://accounts.google.com/gsi/client'

function loadGoogleScript(): Promise<void> {
  if (window.google?.accounts?.id) return Promise.resolve()
  const existing = document.querySelector(`script[src="${SCRIPT_SRC}"]`)
  if (existing) {
    return new Promise((resolve) => existing.addEventListener('load', () => resolve()))
  }
  return new Promise((resolve) => {
    const script = document.createElement('script')
    script.src = SCRIPT_SRC
    script.async = true
    script.defer = true
    script.onload = () => resolve()
    document.head.appendChild(script)
  })
}

/** Renders the official Google Identity Services button and forwards the ID token on sign-in. */
export function GoogleSignInButton({ onIdToken }: { onIdToken: (idToken: string) => void }) {
  const containerId = useId()
  const containerRef = useRef<HTMLDivElement>(null)
  const onIdTokenRef = useRef(onIdToken)

  useEffect(() => {
    onIdTokenRef.current = onIdToken
  })

  useEffect(() => {
    let cancelled = false

    loadGoogleScript().then(() => {
      if (cancelled || !containerRef.current || !window.google) return
      window.google.accounts.id.initialize({
        client_id: GOOGLE_CLIENT_ID,
        callback: (response) => onIdTokenRef.current(response.credential),
      })
      window.google.accounts.id.renderButton(containerRef.current, {
        theme: 'outline',
        size: 'large',
        width: 320,
        text: 'signin_with',
      })
    })

    return () => {
      cancelled = true
    }
  }, [])

  return <div id={containerId} ref={containerRef} className="flex justify-center" />
}
