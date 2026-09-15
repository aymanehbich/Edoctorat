import { Navigate, Route, Routes } from 'react-router-dom'
import { RequireAuth } from '@/auth/RequireAuth'
import { LandingPage } from '@/pages/LandingPage'
import { NotFoundPage } from '@/pages/NotFoundPage'
import { RequestPasswordResetPage } from '@/pages/RequestPasswordResetPage'
import { ResetPasswordPage } from '@/pages/ResetPasswordPage'
import { CandidatLayout } from '@/portals/candidat/layout/CandidatLayout'
import { CandidaturesPage } from '@/portals/candidat/pages/CandidaturesPage'
import { CandidatDashboardPage } from '@/portals/candidat/pages/DashboardPage'
import { CandidatDiplomesPage } from '@/portals/candidat/pages/DiplomesPage'
import { CandidatLoginPage } from '@/portals/candidat/pages/LoginPage'
import { CandidatProfilePage } from '@/portals/candidat/pages/ProfilePage'
import { CandidatRegisterPage } from '@/portals/candidat/pages/RegisterPage'
import { CandidatResultatsPage } from '@/portals/candidat/pages/ResultatsPage'
import { SujetDetailPage } from '@/portals/candidat/pages/SujetDetailPage'
import { SujetsBrowsePage } from '@/portals/candidat/pages/SujetsBrowsePage'
import { ProfesseurLayout } from '@/portals/professeur/layout/ProfesseurLayout'
import { ProfesseurCandidatsPage } from '@/portals/professeur/pages/CandidatsPage'
import { EspaceCedPage } from '@/portals/professeur/pages/CedPage'
import { ProfesseurDashboardPage } from '@/portals/professeur/pages/DashboardPage'
import { ProfesseurInscritsPage } from '@/portals/professeur/pages/InscritsPage'
import { ProfesseurLaboPage } from '@/portals/professeur/pages/LaboPage'
import { ProfesseurLoginPage } from '@/portals/professeur/pages/LoginPage'
import { ProfesseurResultatsPage } from '@/portals/professeur/pages/ResultatsPage'
import { ProfesseurSujetsPage } from '@/portals/professeur/pages/SujetsPage'
import { ScolariteLayout } from '@/portals/scolarite/layout/ScolariteLayout'
import { ScolariteDashboardPage } from '@/portals/scolarite/pages/DashboardPage'
import { ScolariteInscriptionsPage } from '@/portals/scolarite/pages/InscriptionsPage'
import { ScolariteLoginPage } from '@/portals/scolarite/pages/LoginPage'
import { AuthLayout } from '@/portals/shared/layout/AuthLayout'

export function AppRouter() {
  return (
    <Routes>
      <Route path="/" element={<LandingPage />} />
      <Route path="/inscription" element={<CandidatRegisterPage />} />

      <Route element={<AuthLayout />}>
        <Route path="/login/candidat" element={<CandidatLoginPage />} />
        <Route path="/login/professeur" element={<ProfesseurLoginPage />} />
        <Route path="/login/scolarite" element={<ScolariteLoginPage />} />
        <Route path="/mot-de-passe-oublie" element={<RequestPasswordResetPage />} />
        <Route path="/reinitialiser-mot-de-passe" element={<ResetPasswordPage />} />
      </Route>

      <Route element={<RequireAuth portal="candidat" />}>
        <Route element={<CandidatLayout />}>
          <Route path="/candidat" element={<CandidatDashboardPage />} />
          <Route path="/candidat/profil" element={<CandidatProfilePage />} />
          <Route path="/candidat/diplomes" element={<CandidatDiplomesPage />} />
          <Route path="/candidat/sujets" element={<SujetsBrowsePage />} />
          <Route path="/candidat/sujets/:id" element={<SujetDetailPage />} />
          <Route path="/candidat/candidatures" element={<CandidaturesPage />} />
          <Route path="/candidat/resultats" element={<CandidatResultatsPage />} />
        </Route>
      </Route>

      <Route element={<RequireAuth portal="professeur" />}>
        <Route element={<ProfesseurLayout />}>
          <Route path="/professeur" element={<ProfesseurDashboardPage />} />
          <Route path="/professeur/sujets" element={<ProfesseurSujetsPage />} />
          <Route path="/professeur/candidats" element={<ProfesseurCandidatsPage />} />
          <Route path="/professeur/resultats" element={<ProfesseurResultatsPage />} />
          <Route path="/professeur/inscrits" element={<ProfesseurInscritsPage />} />
          <Route path="/ced" element={<EspaceCedPage />} />
          <Route path="/labo" element={<ProfesseurLaboPage />} />
        </Route>
      </Route>

      <Route element={<RequireAuth portal="scolarite" />}>
        <Route element={<ScolariteLayout />}>
          <Route path="/scolarite" element={<ScolariteDashboardPage />} />
          <Route path="/scolarite/inscriptions" element={<ScolariteInscriptionsPage />} />
        </Route>
      </Route>

      <Route path="/404" element={<NotFoundPage />} />
      <Route path="*" element={<Navigate to="/404" replace />} />
    </Routes>
  )
}
