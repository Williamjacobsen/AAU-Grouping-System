
export async function fetchWithDefaultErrorHandling(pagePath, options = {}) {
	try {
		const response = await fetch(
			`${process.env.REACT_APP_API_BASE_URL}` + pagePath,
			{
				...options
			}
		);

		if (!response.ok) {
			return Promise.reject("Status code " + response.status + ": " + await response.text());
		}

		return response;
	} catch (error) {
		return Promise.reject(error);
	}
}