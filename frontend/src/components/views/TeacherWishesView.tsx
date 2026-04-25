'use client'

import {
	Box,
	Button,
	Checkbox,
	CircularProgress,
	FormControl,
	InputLabel,
	MenuItem,
	Paper,
	Select,
	Table,
	TableBody,
	TableCell,
	TableContainer,
	TableHead,
	TableRow,
	Typography,
} from '@mui/material'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useEffect, useState } from 'react'
import {
	fetchDays,
	fetchTeachers,
	fetchTimes,
	fetchTeacherRestrictions,
	updateTeacherRestrictions,
	handleError,
	RestrictedSlot,
} from '@/lib/api/scheduleApi'

export default function TeacherWishesView() {
	const queryClient = useQueryClient()
	const [selectedTeacherId, setSelectedTeacherId] = useState<number | null>(null)
	const [tempRestrictions, setTempRestrictions] = useState<RestrictedSlot[]>([])

	const { data: teachers = [], isLoading: isLoadingTeachers } = useQuery({
		queryKey: ['teachers'],
		queryFn: fetchTeachers,
	})

	const { data: days = [], isLoading: isLoadingDays } = useQuery({
		queryKey: ['days'],
		queryFn: fetchDays,
	})

	const { data: times = [], isLoading: isLoadingTimes } = useQuery({
		queryKey: ['times'],
		queryFn: fetchTimes,
	})

	const { data: initialRestrictions } = useQuery({
		queryKey: ['teacher-restrictions', selectedTeacherId],
		queryFn: () => (selectedTeacherId ? fetchTeacherRestrictions(selectedTeacherId) : null),
		enabled: !!selectedTeacherId,
	})

	useEffect(() => {
		if (initialRestrictions) {
			setTempRestrictions(initialRestrictions.restrictedSlots)
		} else if (!selectedTeacherId) {
			setTempRestrictions([])
		}
	}, [initialRestrictions, selectedTeacherId])

	const updateMutation = useMutation({
		mutationFn: (data: { teacherId: number; restrictions: RestrictedSlot[] }) =>
			updateTeacherRestrictions(data.teacherId, {
				teacherId: data.teacherId,
				restrictedSlots: data.restrictions,
			}),
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: ['teacher-restrictions', selectedTeacherId] })
			alert('Побажання успішно збережено')
		},
		onError: (error) => alert(handleError(error)),
	})

	const isRestricted = (dayId: number, timeId: number) => {
		return tempRestrictions.some((r) => r.dayId === dayId && r.timeId === timeId)
	}

	const toggleRestriction = (dayId: number, timeId: number) => {
		if (isRestricted(dayId, timeId)) {
			setTempRestrictions((prev) => prev.filter((r) => !(r.dayId === dayId && r.timeId === timeId)))
		} else {
			setTempRestrictions((prev) => [...prev, { dayId, timeId }])
		}
	}

	const handleSave = () => {
		if (selectedTeacherId) {
			updateMutation.mutate({ teacherId: selectedTeacherId, restrictions: tempRestrictions })
		}
	}

	if (isLoadingTeachers || isLoadingDays || isLoadingTimes) {
		return <CircularProgress />
	}

	return (
		<Box sx={{ p: 3 }}>
			<Typography variant="h4" gutterBottom>
				Побажання викладачів
			</Typography>

			<Paper sx={{ p: 3, mb: 3 }}>
				<FormControl fullWidth>
					<InputLabel id="teacher-select-label">Виберіть викладача</InputLabel>
					<Select
						labelId="teacher-select-label"
						value={selectedTeacherId || ''}
						label="Виберіть викладача"
						onChange={(e) => setSelectedTeacherId(Number(e.target.value))}
					>
						{teachers.map((teacher) => (
							<MenuItem key={teacher.id} value={teacher.id}>
								{teacher.name} ({teacher.departmentName})
							</MenuItem>
						))}
					</Select>
				</FormControl>
			</Paper>

			{selectedTeacherId && (
				<TableContainer component={Paper}>
					<Box sx={{ p: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
						<Typography variant="h6">Графік доступності</Typography>
						<Button
							variant="contained"
							color="primary"
							onClick={handleSave}
							disabled={updateMutation.isPending}
						>
							{updateMutation.isPending ? 'Збереження...' : 'Зберегти зміни'}
						</Button>
					</Box>
					<Table size="small">
						<TableHead>
							<TableRow>
								<TableCell>Час / День</TableCell>
								{days.map((day) => (
									<TableCell key={day.id} align="center">
										{day.name}
									</TableCell>
								))}
							</TableRow>
						</TableHead>
						<TableBody>
							{times.map((time) => (
								<TableRow key={time.id}>
									<TableCell component="th" scope="row">
										{time.start} - {time.end}
										{time.buildingName && (
											<Typography variant="caption" display="block">
												({time.buildingName})
											</Typography>
										)}
									</TableCell>
									{days.map((day) => (
										<TableCell key={day.id} align="center">
											<Checkbox
												checked={!isRestricted(day.id, time.id)}
												onChange={() => toggleRestriction(day.id, time.id)}
											/>
										</TableCell>
									))}
								</TableRow>
							))}
						</TableBody>
					</Table>
				</TableContainer>
			)}
		</Box>
	)
}
