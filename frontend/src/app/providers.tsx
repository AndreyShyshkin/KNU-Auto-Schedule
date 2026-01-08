'use client'

import { createTheme, CssBaseline, ThemeProvider } from '@mui/material'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { useState } from 'react'

const theme = createTheme({
	palette: {
		mode: 'light',
		primary: {
			main: '#1976d2',
		},
		background: {
			default: '#f5f5f5',
		},
	},
})

export default function Providers({ children }: { children: React.ReactNode }) {
	const [queryClient] = useState(() => new QueryClient())

	return (
		<QueryClientProvider client={queryClient}>
			<ThemeProvider theme={theme}>
				<CssBaseline />
				{children}
			</ThemeProvider>
		</QueryClientProvider>
	)
}
