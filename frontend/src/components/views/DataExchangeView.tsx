'use client'

import {
	Alert,
	AlertTitle,
	Box,
	Button,
	Checkbox,
	CircularProgress,
	Divider,
	FormControlLabel,
	FormGroup,
	List,
	ListItem,
	ListItemText,
	Paper,
	Typography,
} from '@mui/material'
import { useMutation, useQuery } from '@tanstack/react-query'
import { useState } from 'react'
import {
	exportData,
	fetchAvailableTables,
	importData,
} from '../../lib/api/scheduleApi'

export default function DataExchangeView() {
	const [selectedExport, setSelectedExport] = useState<string[]>([])
	const [selectedImport, setSelectedImport] = useState<string[]>([])
	const [file, setFile] = useState<File | null>(null)
	const [importResult, setImportResult] = useState<Record<
		string,
		string
	> | null>(null)

	const { data: availableTables, isLoading } = useQuery({
		queryKey: ['availableTables'],
		queryFn: fetchAvailableTables,
	})

	const exportMutation = useMutation({
		mutationFn: exportData,
	})

	const importMutation = useMutation({
		mutationFn: (vars: { file: File; tables: string[] }) =>
			importData(vars.file, vars.tables),
		onSuccess: (data) => {
			setImportResult(data)
			setFile(null)
		},
		onError: (error) => {
			alert('Import failed: ' + error.message)
		},
	})

	const toggleExport = (table: string) => {
		setSelectedExport((prev) =>
			prev.includes(table) ? prev.filter((t) => t !== table) : [...prev, table]
		)
	}

	const toggleImport = (table: string) => {
		setSelectedImport((prev) =>
			prev.includes(table) ? prev.filter((t) => t !== table) : [...prev, table]
		)
	}

	const handleSelectAllExport = () => {
		if (availableTables) {
			if (selectedExport.length === availableTables.length) {
				setSelectedExport([])
			} else {
				setSelectedExport([...availableTables])
			}
		}
	}

	const handleSelectAllImport = () => {
		if (availableTables) {
			if (selectedImport.length === availableTables.length) {
				setSelectedImport([])
			} else {
				setSelectedImport([...availableTables])
			}
		}
	}

	if (isLoading) return <CircularProgress />

	return (
		<Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
			<Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 2 }}>
				<Paper sx={{ p: 3, flex: '1 1 400px', borderRadius: 2 }} elevation={2}>
					<Typography variant='h6' gutterBottom color='primary'>
						Експорт даних
					</Typography>
					<Typography variant='body2' color='text.secondary' sx={{ mb: 2 }}>
						Оберіть таблиці, які ви бажаєте вивантажити у ZIP-архів.
					</Typography>
					<Button 
						size="small" 
						onClick={handleSelectAllExport}
						sx={{ mb: 1 }}
					>
						{selectedExport.length === availableTables?.length ? 'Зняти виділення' : 'Вибрати все'}
					</Button>
					<Divider sx={{ mb: 2 }} />
					<FormGroup sx={{ maxHeight: 300, overflow: 'auto' }}>
						{availableTables?.map((table) => (
							<FormControlLabel
								key={table}
								control={
									<Checkbox
										checked={selectedExport.includes(table)}
										onChange={() => toggleExport(table)}
										size='small'
									/>
								}
								label={table}
							/>
						))}
					</FormGroup>
					<Button
						variant='contained'
						fullWidth
						onClick={() => exportMutation.mutate(selectedExport)}
						disabled={selectedExport.length === 0 || exportMutation.isPending}
						sx={{ mt: 3 }}
					>
						{exportMutation.isPending ? <CircularProgress size={24} /> : 'Скачати ZIP'}
					</Button>
				</Paper>

				<Paper sx={{ p: 3, flex: '1 1 400px', borderRadius: 2 }} elevation={2}>
					<Typography variant='h6' gutterBottom color='secondary'>
						Імпорт даних
					</Typography>
					<Typography variant='body2' color='text.secondary' sx={{ mb: 2 }}>
						Завантажте ZIP-архів та оберіть таблиці для імпорту.
					</Typography>
					<Button 
						size="small" 
						onClick={handleSelectAllImport}
						sx={{ mb: 1 }}
					>
						{selectedImport.length === availableTables?.length ? 'Зняти виділення' : 'Вибрати все'}
					</Button>
					<Divider sx={{ mb: 2 }} />
					<Box sx={{ mb: 3 }}>
						<Button
							variant='outlined'
							component='label'
							fullWidth
							color={file ? 'success' : 'primary'}
						>
							{file ? `Файл: ${file.name}` : 'Вибрати ZIP файл'}
							<input
								type='file'
								hidden
								accept='.zip'
								onChange={(e) => {
									setFile(e.target.files?.[0] || null)
									setImportResult(null)
								}}
							/>
						</Button>
					</Box>
					<FormGroup sx={{ maxHeight: 300, overflow: 'auto' }}>
						{availableTables?.map((table) => (
							<FormControlLabel
								key={table}
								control={
									<Checkbox
										checked={selectedImport.includes(table)}
										onChange={() => toggleImport(table)}
										size='small'
									/>
								}
								label={table}
							/>
						))}
					</FormGroup>
					<Button
						variant='contained'
						color='secondary'
						fullWidth
						onClick={() =>
							file && importMutation.mutate({ file, tables: selectedImport })
						}
						disabled={
							!file || selectedImport.length === 0 || importMutation.isPending
						}
						sx={{ mt: 3 }}
					>
						{importMutation.isPending ? <CircularProgress size={24} /> : 'Почати імпорт'}
					</Button>
				</Paper>
			</Box>

			{importResult && (
				<Alert 
					severity="success" 
					onClose={() => setImportResult(null)}
					sx={{ borderRadius: 2 }}
				>
					<AlertTitle>Результати імпорту</AlertTitle>
					<List dense>
						{Object.entries(importResult).map(([table, message]) => (
							<ListItem key={table}>
								<ListItemText 
									primary={<strong>{table}</strong>} 
									secondary={message} 
								/>
							</ListItem>
						))}
					</List>
				</Alert>
			)}
		</Box>
	)
}
