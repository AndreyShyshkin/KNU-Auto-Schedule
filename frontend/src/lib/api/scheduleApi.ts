import axios from 'axios'

// --- Interfaces ---

export interface Building {
	id: number
	name: string
	description: string
}

export interface LessonType {
	id: number
	name: string
}

export interface Faculty {
	id: number
	name: string
	description: string
}

export interface Subject {
	id: number
	name: string
	facultyId: number
	facultyName: string
}

export interface Chair {
	id: number
	name: string
	description: string
	facultyId: number
	facultyName: string
}

export interface Speciality {
	id: number
	name: string
	description: string
	facultyId: number
	facultyName: string
}

export interface Teacher {
	id: number
	name: string
	departmentId: number
	departmentName: string
}

export interface Group {
	id: number
	name: string
	departmentId: number
	departmentName: string
	year: number
	size: number
}

export interface Lesson {
	id: number
	subjectId: number
	subjectName: string
	earmarkId: number
	earmarkName: string
	auditoriumId?: number
	auditoriumName?: string
	buildingId?: number
	buildingName?: string
	online: boolean
	onlineLink: string
	lessonTypeIds: number[]
	lessonTypeNames: string[]
	count: number
	allowMultipleAuditoriums: boolean
	weekFrequency: number
	totalHours: number
	startDate?: string
	endDate?: string
	teacherIds: number[]
	teacherNames: string[]
	groupIds: number[]
	groupNames: string[]
}

export interface Auditorium {
	id: number
	name: string
	earmarkId: number
	earmarkName: string
	buildingId?: number
	buildingName?: string
}

export interface Earmark {
	id: number
	name: string
	size: number
	buildingId?: number
	buildingName?: string
}

export interface Time {
	id: number
	start: string
	end: string
	buildingId?: number
	buildingName?: string
}

export interface Day {
	id: number
	name: string
	dayOfWeek: number
	times: Time[]
}

export interface ScheduleVersion {
	id: number
	name: string
	createdAt: string
	current: boolean
	startDate?: string
	endDate?: string
}

export interface ScheduleEntryDto {
	dayName: string
	timeStart: string
	timeEnd: string
	subjectName: string
	lessonTypeName: string
	earmarkName: string
	buildingName: string
	auditoriumName: string
	additionalInfo: string
	actualDate?: string
}

export interface User {
	id: number
	username: string
	password?: string
	role: string
}

export interface BuildStatus {
	building: boolean
	lastResult: string
	lastError: string
	steps: number
}

// --- API Methods ---

// Lesson Types
export const fetchLessonTypes = async (): Promise<LessonType[]> => {
	const { data } = await axios.get('/api/lesson-types')
	return data
}
export const createLessonType = async (type: Partial<LessonType>): Promise<LessonType> => {
	const { data } = await axios.post('/api/lesson-types', type)
	return data
}
export const updateLessonType = async (id: number, type: Partial<LessonType>): Promise<LessonType> => {
	const { data } = await axios.put(`/api/lesson-types/${id}`, type)
	return data
}
export const deleteLessonType = async (id: number): Promise<void> => {
	await axios.delete(`/api/lesson-types/${id}`)
}

// Buildings
export const fetchBuildings = async (): Promise<Building[]> => {
	const { data } = await axios.get('/api/buildings')
	return data
}
export const createBuilding = async (building: Partial<Building>): Promise<Building> => {
	const { data } = await axios.post('/api/buildings', building)
	return data
}
export const updateBuilding = async (id: number, building: Partial<Building>): Promise<Building> => {
	const { data } = await axios.put(`/api/buildings/${id}`, building)
	return data
}
export const deleteBuilding = async (id: number): Promise<void> => {
	await axios.delete(`/api/buildings/${id}`)
}

// Faculties
export const fetchFaculties = async (): Promise<Faculty[]> => {
	const { data } = await axios.get('/api/faculties')
	return data
}
export const createFaculty = async (faculty: Partial<Faculty>): Promise<Faculty> => {
	const { data } = await axios.post('/api/faculties', faculty)
	return data
}
export const updateFaculty = async (id: number, faculty: Partial<Faculty>): Promise<Faculty> => {
	const { data } = await axios.put(`/api/faculties/${id}`, faculty)
	return data
}
export const deleteFaculty = async (id: number): Promise<void> => {
	await axios.delete(`/api/faculties/${id}`)
}

// Subjects
export const fetchSubjects = async (): Promise<Subject[]> => {
	const { data } = await axios.get('/api/subjects')
	return data
}
export const createSubject = async (subject: Partial<Subject>): Promise<Subject> => {
	const { data } = await axios.post('/api/subjects', subject)
	return data
}
export const updateSubject = async (id: number, subject: Partial<Subject>): Promise<Subject> => {
	const { data } = await axios.put(`/api/subjects/${id}`, subject)
	return data
}
export const deleteSubject = async (id: number): Promise<void> => {
	await axios.delete(`/api/subjects/${id}`)
}

// Chairs
export const fetchChairs = async (): Promise<Chair[]> => {
	const { data } = await axios.get('/api/chairs')
	return data
}
export const createChair = async (chair: Partial<Chair>): Promise<Chair> => {
	const { data } = await axios.post('/api/chairs', chair)
	return data
}
export const updateChair = async (id: number, chair: Partial<Chair>): Promise<Chair> => {
	const { data } = await axios.put(`/api/chairs/${id}`, chair)
	return data
}
export const deleteChair = async (id: number): Promise<void> => {
	await axios.delete(`/api/chairs/${id}`)
}

// Specialities
export const fetchSpecialities = async (): Promise<Speciality[]> => {
	const { data } = await axios.get('/api/specialities')
	return data
}
export const createSpeciality = async (speciality: Partial<Speciality>): Promise<Speciality> => {
	const { data } = await axios.post('/api/specialities', speciality)
	return data
}
export const updateSpeciality = async (id: number, speciality: Partial<Speciality>): Promise<Speciality> => {
	const { data } = await axios.put(`/api/specialities/${id}`, speciality)
	return data
}
export const deleteSpeciality = async (id: number): Promise<void> => {
	await axios.delete(`/api/specialities/${id}`)
}

// Teachers
export const fetchTeachers = async (): Promise<Teacher[]> => {
	const { data } = await axios.get('/api/teachers')
	return data
}
export const createTeacher = async (teacher: Partial<Teacher>): Promise<Teacher> => {
	const { data } = await axios.post('/api/teachers', teacher)
	return data
}
export const updateTeacher = async (id: number, teacher: Partial<Teacher>): Promise<Teacher> => {
	const { data } = await axios.put(`/api/teachers/${id}`, teacher)
	return data
}
export const deleteTeacher = async (id: number): Promise<void> => {
	await axios.delete(`/api/teachers/${id}`)
}

// Groups
export const fetchGroups = async (): Promise<Group[]> => {
	const { data } = await axios.get('/api/groups')
	return data
}
export const createGroup = async (group: Partial<Group>): Promise<Group> => {
	const { data } = await axios.post('/api/groups', group)
	return data
}
export const updateGroup = async (id: number, group: Partial<Group>): Promise<Group> => {
	const { data } = await axios.put(`/api/groups/${id}`, group)
	return data
}
export const deleteGroup = async (id: number): Promise<void> => {
	await axios.delete(`/api/groups/${id}`)
}

// Lessons
export const fetchLessons = async (): Promise<Lesson[]> => {
	const { data } = await axios.get('/api/lessons')
	return data
}
export const createLesson = async (lesson: Partial<Lesson>): Promise<Lesson> => {
	const { data } = await axios.post('/api/lessons', lesson)
	return data
}
export const updateLesson = async (id: number, lesson: Partial<Lesson>): Promise<Lesson> => {
	const { data } = await axios.put(`/api/lessons/${id}`, lesson)
	return data
}
export const deleteLesson = async (id: number): Promise<void> => {
	await axios.delete(`/api/lessons/${id}`)
}

// Auditoriums
export const fetchAuditoriums = async (): Promise<Auditorium[]> => {
	const { data } = await axios.get('/api/auditoriums')
	return data
}
export const createAuditorium = async (auditorium: Partial<Auditorium>): Promise<Auditorium> => {
	const { data } = await axios.post('/api/auditoriums', auditorium)
	return data
}
export const updateAuditorium = async (id: number, auditorium: Partial<Auditorium>): Promise<Auditorium> => {
	const { data } = await axios.put(`/api/auditoriums/${id}`, auditorium)
	return data
}
export const deleteAuditorium = async (id: number): Promise<void> => {
	await axios.delete(`/api/auditoriums/${id}`)
}

// Earmarks
export const fetchEarmarks = async (): Promise<Earmark[]> => {
	const { data } = await axios.get('/api/earmarks')
	return data
}
export const createEarmark = async (earmark: Partial<Earmark>): Promise<Earmark> => {
	const { data } = await axios.post('/api/earmarks', earmark)
	return data
}
export const updateEarmark = async (id: number, earmark: Partial<Earmark>): Promise<Earmark> => {
	const { data } = await axios.put(`/api/earmarks/${id}`, earmark)
	return data
}
export const deleteEarmark = async (id: number): Promise<void> => {
	await axios.delete(`/api/earmarks/${id}`)
}

// Times
export const fetchTimes = async (): Promise<Time[]> => {
	const { data } = await axios.get('/api/times')
	return data
}
export const createTime = async (time: Partial<Time>): Promise<Time> => {
	const { data } = await axios.post('/api/times', time)
	return data
}
export const updateTime = async (id: number, time: Partial<Time>): Promise<Time> => {
	const { data } = await axios.put(`/api/times/${id}`, time)
	return data
}
export const deleteTime = async (id: number): Promise<void> => {
	await axios.delete(`/api/times/${id}`)
}

// Days
export const fetchDays = async (): Promise<Day[]> => {
	const { data } = await axios.get('/api/days')
	return data
}
export const createDay = async (day: Partial<Day>): Promise<Day> => {
	const { data } = await axios.post('/api/days', day)
	return data
}
export const updateDay = async (id: number, day: Partial<Day>): Promise<Day> => {
	const { data } = await axios.put(`/api/days/${id}`, day)
	return data
}
export const deleteDay = async (id: number): Promise<void> => {
	await axios.delete(`/api/days/${id}`)
}

// Teacher Restrictions
export interface RestrictedSlot {
	dayId: number
	timeId: number
}
export interface TeacherRestrictions {
	teacherId: number
	restrictedSlots: RestrictedSlot[]
}
export const fetchTeacherRestrictions = async (teacherId: number): Promise<TeacherRestrictions> => {
	const { data } = await axios.get(`/api/teachers/${teacherId}/restrictions`)
	return data
}
export const updateTeacherRestrictions = async (teacherId: number, restrictions: TeacherRestrictions): Promise<void> => {
	await axios.put(`/api/teachers/${teacherId}/restrictions`, restrictions)
}

// Build
export const startBuild = async (startDate?: string, endDate?: string): Promise<string> => {
	const { data } = await axios.post('/api/build/start', null, {
		params: { startDate, endDate }
	})
	return data
}
export const getBuildStatus = async (): Promise<BuildStatus> => {
	const { data } = await axios.get('/api/build/status')
	return data
}

// Schedule Versions
export const fetchScheduleVersions = async (): Promise<ScheduleVersion[]> => {
	const { data } = await axios.get('/api/schedule-versions')
	return data
}
export const deleteScheduleVersion = async (id?: number): Promise<void> => {
	if (id) {
		await axios.delete(`/api/schedule-versions/${id}`)
	} else {
		await axios.delete('/api/schedule/clear')
	}
}

// Schedule Query
export const fetchTeacherSchedule = async (teacherId: number, versionId?: number): Promise<ScheduleEntryDto[]> => {
	const { data } = await axios.get(`/api/schedule/teacher/${teacherId}`, { params: { versionId } })
	return data
}
export const fetchGroupSchedule = async (groupId: number, versionId?: number): Promise<ScheduleEntryDto[]> => {
	const { data } = await axios.get(`/api/schedule/group/${groupId}`, { params: { versionId } })
	return data
}
export const fetchAllSchedule = async (versionId?: number): Promise<ScheduleEntryDto[]> => {
	const { data } = await axios.get('/api/schedule/all', { params: { versionId } })
	return data
}

// Data Exchange
export const fetchTables = async (): Promise<string[]> => {
	const { data } = await axios.get('/api/data/export/available')
	return data
}
export const fetchAvailableTables = fetchTables

export const exportTables = async (tables: string[]): Promise<void> => {
	const { data } = await axios.post('/api/data/export', tables, {
		responseType: 'blob',
	})
	const url = window.URL.createObjectURL(new Blob([data]))
	const link = document.createElement('a')
	link.href = url
	link.setAttribute('download', 'schedule_data.zip')
	document.body.appendChild(link)
	link.click()
	link.remove()
}
export const exportData = exportTables

export const importTables = async (file: File, tables: string[]): Promise<Record<string, string>> => {
	const formData = new FormData()
	formData.append('file', file)
	formData.append('tables', tables.join(','))
	const { data } = await axios.post('/api/data/import', formData)
	return data
}
export const importData = importTables

// Admin Users
export const fetchUsers = async (): Promise<User[]> => {
	const { data } = await axios.get('/api/admin/users')
	return data
}
export const fetchAdminUsers = fetchUsers

export const createUser = async (user: Partial<User>): Promise<User> => {
	const { data } = await axios.post('/api/admin/users', user)
	return data
}
export const createAdminUser = createUser

export const deleteUser = async (id: number): Promise<void> => {
	await axios.delete(`/api/admin/users/${id}`)
}
export const deleteAdminUser = deleteUser

// Excel/PDF Exports
export const exportScheduleExcel = async (data: any[], title: string): Promise<void> => {
	const { data: blob } = await axios.post('/api/schedule/export/excel', data, {
		params: { title },
		responseType: 'blob',
	})
	const url = window.URL.createObjectURL(new Blob([blob]))
	const link = document.createElement('a')
	link.href = url
	link.setAttribute('download', `${title}.xlsx`)
	document.body.appendChild(link)
	link.click()
	link.remove()
}

export const exportSchedulePdf = async (data: any[], title: string): Promise<void> => {
	const { data: blob } = await axios.post('/api/schedule/export/pdf', data, {
		params: { title },
		responseType: 'blob',
	})
	const url = window.URL.createObjectURL(new Blob([blob]))
	const link = document.createElement('a')
	link.href = url
	link.setAttribute('download', `${title}.pdf`)
	document.body.appendChild(link)
	link.click()
	link.remove()
}

export const handleError = (error: any): string => {
	if (error.response?.data?.message) {
		return error.response.data.message
	}
	return error.message || 'Виникла невідома помилка'
}
