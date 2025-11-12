import React, { memo } from "react";

const ResetFormButton = memo(() => {
	return (
		<div className="button-group">
			<button
				className="button-secondary"
				type="button"
				onClick={() => window.location.reload()}
			>
				Reset changes to form
			</button>
		</div>
	);
});

export default ResetFormButton;