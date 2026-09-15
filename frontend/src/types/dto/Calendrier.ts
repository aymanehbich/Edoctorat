/** GET /api/get-calendrier/ returns the raw entity list (no DTO). */
export interface Calendrier {
  id: number
  /** LocalDate on the backend -> "yyyy-MM-dd" */
  dateDebut: string
  dateFin: string
  pour: string
  action: string
}

/** PATCH /api/update-canlendrier/{id}/ request body (note the URL typo: "canlendrier"). */
export interface UpdateCalendrierRequest {
  dateDebut: string
  dateFin: string
}
