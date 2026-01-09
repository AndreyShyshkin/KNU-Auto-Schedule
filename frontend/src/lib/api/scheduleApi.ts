import axios from 'axios'

export interface Faculty {
	id: number
	name: string
	description: string
}

export interface Chair {
	id: number
	name: string
	description: string
	facultyId: number
	facultyName: string
}

export const fetchFaculties = async (): Promise<Faculty[]> => {
	const { data } = await axios.get('/api/faculties')
	return data
}

export const createFaculty = async (
	faculty: Partial<Faculty>
): Promise<Faculty> => {
	const { data } = await axios.post('/api/faculties', faculty)
	return data
}

export const updateFaculty = async (
	id: number,
	faculty: Partial<Faculty>
): Promise<Faculty> => {
	const { data } = await axios.put(`/api/faculties/${id}`, faculty)
	return data
}

export const deleteFaculty = async (id: number): Promise<void> => {
	await axios.delete(`/api/faculties/${id}`)
}

export const fetchChairs = async (): Promise<Chair[]> => {
	const { data } = await axios.get('/api/chairs')
	return data
}

export const createChair = async (chair: Partial<Chair>): Promise<Chair> => {
	const { data } = await axios.post('/api/chairs', chair)
	return data
}

export const updateChair = async (
	id: number,
	chair: Partial<Chair>
): Promise<Chair> => {
	const { data } = await axios.put(`/api/chairs/${id}`, chair)
	return data
}

export const deleteChair = async (id: number): Promise<void> => {
	await axios.delete(`/api/chairs/${id}`)
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

export interface Subject {
	id: number
	name: string
}

export const fetchSpecialities = async (): Promise<Speciality[]> => {
	const { data } = await axios.get('/api/specialities')
	return data
}

export const fetchTeachers = async (): Promise<Teacher[]> => {
	const { data } = await axios.get('/api/teachers')
	return data
}

export const fetchGroups = async (): Promise<Group[]> => {
	const { data } = await axios.get('/api/groups')
	return data
}

export const fetchSubjects = async (): Promise<Subject[]> => {
	const { data } = await axios.get('/api/subjects')
	return data
}

// --- Speciality ---
export const createSpeciality = async (
	speciality: Partial<Speciality>
): Promise<Speciality> => {
	const { data } = await axios.post('/api/specialities', speciality)
	return data
}

export const updateSpeciality = async (
	id: number,
	speciality: Partial<Speciality>
): Promise<Speciality> => {
	const { data } = await axios.put(`/api/specialities/${id}`, speciality)
	return data
}

export const deleteSpeciality = async (id: number): Promise<void> => {
	await axios.delete(`/api/specialities/${id}`)
}

// --- Teacher ---
export const createTeacher = async (
	teacher: Partial<Teacher>
): Promise<Teacher> => {
	const { data } = await axios.post('/api/teachers', teacher)
	return data
}

export const updateTeacher = async (
	id: number,
	teacher: Partial<Teacher>
): Promise<Teacher> => {
	const { data } = await axios.put(`/api/teachers/${id}`, teacher)
	return data
}

export const deleteTeacher = async (id: number): Promise<void> => {
	await axios.delete(`/api/teachers/${id}`)
}

// --- Group ---
export const createGroup = async (group: Partial<Group>): Promise<Group> => {
	const { data } = await axios.post('/api/groups', group)
	return data
}

export const updateGroup = async (
	id: number,
	group: Partial<Group>
): Promise<Group> => {
	const { data } = await axios.put(`/api/groups/${id}`, group)
	return data
}

export const deleteGroup = async (id: number): Promise<void> => {
	await axios.delete(`/api/groups/${id}`)
}

// --- Subject ---
export const createSubject = async (
	subject: Partial<Subject>
): Promise<Subject> => {
	const { data } = await axios.post('/api/subjects', subject)
	return data
}

export const updateSubject = async (
	id: number,
	subject: Partial<Subject>
): Promise<Subject> => {
	const { data } = await axios.put(`/api/subjects/${id}`, subject)
	return data
}

export const deleteSubject = async (id: number): Promise<void> => {
	await axios.delete(`/api/subjects/${id}`)
}

// --- Earmark ---
export const createEarmark = async (
	earmark: Partial<Earmark>
): Promise<Earmark> => {
	const { data } = await axios.post('/api/earmarks', earmark)
	return data
}

export const updateEarmark = async (
	id: number,
	earmark: Partial<Earmark>
): Promise<Earmark> => {
	const { data } = await axios.put(`/api/earmarks/${id}`, earmark)
	return data
}

export const deleteEarmark = async (id: number): Promise<void> => {
	await axios.delete(`/api/earmarks/${id}`)
}

// --- Auditorium ---
export const createAuditorium = async (
	auditorium: Partial<Auditorium>
): Promise<Auditorium> => {
	const { data } = await axios.post('/api/auditoriums', auditorium)
	return data
}

export const updateAuditorium = async (
	id: number,
	auditorium: Partial<Auditorium>
): Promise<Auditorium> => {
	const { data } = await axios.put(`/api/auditoriums/${id}`, auditorium)
	return data
}

export const deleteAuditorium = async (id: number): Promise<void> => {
	await axios.delete(`/api/auditoriums/${id}`)
}

export interface Earmark {
	id: number
	name: string
	size: number
}

export interface Auditorium {
	id: number
	name: string
	earmarkId: number
	earmarkName: string
}

export interface Time {
	id: number
	start: string
	end: string
}

// --- Day ---
export const createDay = async (day: Partial<Day>): Promise<Day> => {
	const { data } = await axios.post('/api/days', day)
	return data
}

export const updateDay = async (
	id: number,
	day: Partial<Day>
): Promise<Day> => {
	const { data } = await axios.put(`/api/days/${id}`, day)
	return data
}

export const deleteDay = async (id: number): Promise<void> => {
	await axios.delete(`/api/days/${id}`)
}

// --- Time ---
export const fetchTimes = async (): Promise<Time[]> => {
	const { data } = await axios.get('/api/times')
	return data
}

export const createTime = async (time: Partial<Time>): Promise<Time> => {
	const { data } = await axios.post('/api/times', time)
	return data
}

export const updateTime = async (
	id: number,
	time: Partial<Time>
): Promise<Time> => {
	const { data } = await axios.put(`/api/times/${id}`, time)
	return data
}

export const deleteTime = async (id: number): Promise<void> => {
	await axios.delete(`/api/times/${id}`)
}

export interface Day {
	id: number
	name: string
	times: Time[]
}

export const fetchDays = async (): Promise<Day[]> => {
	const { data } = await axios.get('/api/days')
	return data
}

export const fetchEarmarks = async (): Promise<Earmark[]> => {
	const { data } = await axios.get('/api/earmarks')
	return data
}

export const fetchAuditoriums = async (): Promise<Auditorium[]> => {
	const { data } = await axios.get('/api/auditoriums')
	return data
}

export interface Lesson {
	id: number
	subjectId: number
	subjectName: string
	earmarkId: number
	earmarkName: string
	count: number
	teacherIds: number[]
	teacherNames: string[]
	groupIds: number[]
	groupNames: string[]
}

// --- Lesson ---
export const fetchLessons = async (): Promise<Lesson[]> => {
	const { data } = await axios.get('/api/lessons')
	return data
}

export const createLesson = async (
	lesson: Partial<Lesson>
): Promise<Lesson> => {
	const { data } = await axios.post('/api/lessons', lesson)
	return data
}

export const updateLesson = async (
	id: number,
	lesson: Partial<Lesson>
): Promise<Lesson> => {
	const { data } = await axios.put(`/api/lessons/${id}`, lesson)
	return data
}

export const deleteLesson = async (id: number): Promise<void> => {
	await axios.delete(`/api/lessons/${id}`)
}

// --- Build ---
export const startBuild = async (): Promise<string> => {
	const { data } = await axios.post('/api/build/start')
	return data
}

export const getBuildStatus = async (): Promise<boolean> => {
	const { data } = await axios.get('/api/build/status')
	return data
}

// --- Schedule ---
export interface ScheduleEntry {
	dayName: string
	timeStart: string
	timeEnd: string
	subjectName: string
	earmarkName: string
	auditoriumName: string
	additionalInfo: string
}

export const fetchTeacherSchedule = async (
	teacherId: number
): Promise<ScheduleEntry[]> => {
	const { data } = await axios.get(`/api/schedule/teacher/${teacherId}`)
	return data
}

export const fetchGroupSchedule = async (
	groupId: number
): Promise<ScheduleEntry[]> => {
	const { data } = await axios.get(`/api/schedule/group/${groupId}`)
	return data
}
