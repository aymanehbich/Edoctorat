import { BrowserRouter } from 'react-router-dom'
import { AppRouter } from '@/app/router'
import { AppProviders } from '@/app/providers'
import { useBootstrapSession } from '@/auth/useBootstrapSession'

function App() {
  useBootstrapSession()

  return (
    <AppProviders>
      <BrowserRouter>
        <AppRouter />
      </BrowserRouter>
    </AppProviders>
  )
}

export default App
