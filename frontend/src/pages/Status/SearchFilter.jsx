import React from "react";


export default function SearchFilter({ searchString, setSearchString }) {

	return (
		<div className="search-filter-input">
			<input
				defaultValue={searchString}
				placeholder={"Search..."}
				onChange={(event) => setSearchString(event.target.value.toLowerCase())}
			/>
		</div>
	);
}
