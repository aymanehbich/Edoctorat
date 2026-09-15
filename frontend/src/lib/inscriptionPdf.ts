import { jsPDF } from 'jspdf'
import type { InscriptionDTO } from '@/types/dto/InscriptionDTO'

/** Generates and downloads an "attestation de dépôt de dossier d'inscription" PDF for one inscription. */
export function downloadAttestationInscription(inscription: InscriptionDTO) {
  const doc = new jsPDF()
  const { candidat, sujet } = inscription
  const marginX = 20
  let y = 25

  doc.setFontSize(16)
  doc.setFont('helvetica', 'bold')
  doc.text('Attestation de dépôt de dossier d\'inscription', marginX, y)

  y += 15
  doc.setFontSize(11)
  doc.setFont('helvetica', 'normal')

  const line = (label: string, value: string) => {
    doc.setFont('helvetica', 'bold')
    doc.text(`${label} :`, marginX, y)
    doc.setFont('helvetica', 'normal')
    doc.text(value, marginX + 55, y)
    y += 8
  }

  line('Candidat', `${candidat.prenom} ${candidat.nom}`)
  line('CNE', candidat.cne)
  line('Sujet de thèse', sujet.titre)
  line('Directeur de thèse', `${sujet.professeur.prenom} ${sujet.professeur.nom}`)
  line('Formation doctorale', sujet.formationDoctorale.titre)
  line('Date de dépôt', inscription.dateDiposeDossier)
  line('Statut', inscription.valider ? 'Inscription validée' : 'En attente de validation')

  if (inscription.remarque) {
    y += 4
    doc.setFont('helvetica', 'bold')
    doc.text('Remarque :', marginX, y)
    y += 7
    doc.setFont('helvetica', 'normal')
    const wrapped = doc.splitTextToSize(inscription.remarque, 170)
    doc.text(wrapped, marginX, y)
    y += wrapped.length * 6
  }

  y += 15
  doc.setFontSize(9)
  doc.setTextColor(120)
  doc.text(`Document généré le ${new Date().toLocaleDateString('fr-FR')}.`, marginX, y)

  doc.save(`attestation-inscription-${sujet.id}.pdf`)
}
