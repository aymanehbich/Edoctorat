import { GraduationCapIcon } from 'lucide-react'
import { Link, Outlet } from 'react-router-dom'

export function AuthLayout() {
  return (
    <div className="flex min-h-screen items-center justify-center bg-muted/40 px-4 py-12">
      <div className="w-full max-w-md space-y-6">
        <Link to="/" className="flex items-center justify-center gap-2 text-foreground">
          <GraduationCapIcon className="size-7 text-primary" />
          <span className="text-lg font-semibold">e-Doctorat</span>
        </Link>
        <Outlet />
      </div>
    </div>
  )
}
