package dk.medcom.video.api.context;

public enum UserRole {
	UNDEFINED {
		@Override
		public UserRole claimRole(String email, String orgainisationId) {
			return UNDEFINED;
		}
	},
	UNAUTHORIZED {
		@Override
		public UserRole claimRole(String email, String orgainisationId) {
			return UNAUTHORIZED;
		}
	},
	USER {
		@Override
		public UserRole claimRole(String email, String orgainisationId) {
			if ((email == null ) || (email.isEmpty()) || (orgainisationId == null) || (orgainisationId.isEmpty())) {
				return UNAUTHORIZED;
			} else {
				return USER;
			}
		}
	},
	ADMIN {
		@Override
		public UserRole claimRole(String email, String orgainisationId) {
			if ((email == null ) || (email.isEmpty()) || (orgainisationId == null) || (orgainisationId.isEmpty())) {
				return UNAUTHORIZED;
			} else {
				return ADMIN;
			}
		}
	},
	PROVISIONER {
		@Override
		public UserRole claimRole(String email, String orgainisationId) {
			if ((email == null ) || (email.isEmpty()) || (orgainisationId == null) || (orgainisationId.isEmpty())) {
				return PROVISIONER;
			} else {
				return PROVISONER_USER;
			}
		}
	},
	PROVISONER_USER {
		@Override
		public UserRole claimRole(String email, String orgainisationId) {
			if ((email == null ) || (email.isEmpty()) || (orgainisationId == null) || (orgainisationId.isEmpty())) {
				return UNAUTHORIZED;
			} else {
				return PROVISONER_USER;
			}
		}
	};

	public abstract UserRole claimRole(String email, String orgainisationId);
}
