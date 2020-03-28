package dk.medcom.video.api.context;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class WspUserContextTest {

	
	WspUserContext subject;
	
	
	@Before
	public void setupTest() {
		
		subject = new WspUserContext();
	}
	
	@Test
	public void testParseSessionDataValueFromLegalHeaderValue() {
		
		// Given
		String headerValue = "eyJJRCI6IjVlNzBjMWU1YmZjZjI3ODMxYTExOTg3NiIsIlNlc3Npb25pZCI6ImEyNjkzNzA4LTdmMzAtNGUxZS04YjBmLTZkZjQyZWFkNzU2MiIsIkF1dGhlbnRpY2F0aW9udG9rZW4iOiJQSE5oYld3eU9rRnpjMlZ5ZEdsdmJpQjRiV3h1Y3pwellXMXNNajBpZFhKdU9tOWhjMmx6T201aGJXVnpPblJqT2xOQlRVdzZNaTR3T21GemMyVnlkR2x2YmlJZ2VHMXNibk02ZUhNOUltaDBkSEE2THk5M2QzY3Vkek11YjNKbkx6SXdNREV2V0UxTVUyTm9aVzFoSWlCNGJXeHVjenA0YzJrOUltaDBkSEE2THk5M2QzY3Vkek11YjNKbkx6SXdNREV2V0UxTVUyTm9aVzFoTFdsdWMzUmhibU5sSWlCSlJEMGlYelV3T1RNM1pXSmlMV1kyWVRBdE5HTTRPUzFpWlRNd0xUVXpaakppWVRVeVpqWmlNaUlnU1hOemRXVkpibk4wWVc1MFBTSXlNREl3TFRBekxURTNWREV5T2pJMk9qRXlMamswTUZvaUlGWmxjbk5wYjI0OUlqSXVNQ0lnZUhOcE9uUjVjR1U5SW5OaGJXd3lPa0Z6YzJWeWRHbHZibFI1Y0dVaVBqeHpZVzFzTWpwSmMzTjFaWEkrYzNSellUd3ZjMkZ0YkRJNlNYTnpkV1Z5UGp4a2N6cFRhV2R1WVhSMWNtVWdlRzFzYm5NNlpITTlJbWgwZEhBNkx5OTNkM2N1ZHpNdWIzSm5Mekl3TURBdk1Ea3ZlRzFzWkhOcFp5TWlQanhrY3pwVGFXZHVaV1JKYm1adlBqeGtjenBEWVc1dmJtbGpZV3hwZW1GMGFXOXVUV1YwYUc5a0lFRnNaMjl5YVhSb2JUMGlhSFIwY0RvdkwzZDNkeTUzTXk1dmNtY3ZNakF3TVM4eE1DOTRiV3d0WlhoakxXTXhORzRqSWk4K1BHUnpPbE5wWjI1aGRIVnlaVTFsZEdodlpDQkJiR2R2Y21sMGFHMDlJbWgwZEhBNkx5OTNkM2N1ZHpNdWIzSm5Mekl3TURBdk1Ea3ZlRzFzWkhOcFp5TnljMkV0YzJoaE1TSXZQanhrY3pwU1pXWmxjbVZ1WTJVZ1ZWSkpQU0lqWHpVd09UTTNaV0ppTFdZMllUQXROR000T1MxaVpUTXdMVFV6WmpKaVlUVXlaalppTWlJK1BHUnpPbFJ5WVc1elptOXliWE0rUEdSek9sUnlZVzV6Wm05eWJTQkJiR2R2Y21sMGFHMDlJbWgwZEhBNkx5OTNkM2N1ZHpNdWIzSm5Mekl3TURBdk1Ea3ZlRzFzWkhOcFp5TmxiblpsYkc5d1pXUXRjMmxuYm1GMGRYSmxJaTgrUEdSek9sUnlZVzV6Wm05eWJTQkJiR2R2Y21sMGFHMDlJbWgwZEhBNkx5OTNkM2N1ZHpNdWIzSm5Mekl3TURFdk1UQXZlRzFzTFdWNFl5MWpNVFJ1SXlJK1BHVmpPa2x1WTJ4MWMybDJaVTVoYldWemNHRmpaWE1nZUcxc2JuTTZaV005SW1oMGRIQTZMeTkzZDNjdWR6TXViM0puTHpJd01ERXZNVEF2ZUcxc0xXVjRZeTFqTVRSdUl5SWdVSEpsWm1sNFRHbHpkRDBpZUhNaUx6NDhMMlJ6T2xSeVlXNXpabTl5YlQ0OEwyUnpPbFJ5WVc1elptOXliWE0rUEdSek9rUnBaMlZ6ZEUxbGRHaHZaQ0JCYkdkdmNtbDBhRzA5SW1oMGRIQTZMeTkzZDNjdWR6TXViM0puTHpJd01EQXZNRGt2ZUcxc1pITnBaeU56YUdFeElpOCtQR1J6T2tScFoyVnpkRlpoYkhWbFBuQlhRV3BSY2xOYUsyMVVZbWRGZW01cmJsWTJZMDl1SzNCS1VUMDhMMlJ6T2tScFoyVnpkRlpoYkhWbFBqd3ZaSE02VW1WbVpYSmxibU5sUGp3dlpITTZVMmxuYm1Wa1NXNW1iejQ4WkhNNlUybG5ibUYwZFhKbFZtRnNkV1UrWms5RGRDdDNPVmhCUWtSV1FVNDFORW95YlVJeU5teG1NMmt5VFRoeVpETmlOazlwY2psRmJVbFBhbVo1Wm1KcmFsaDZaR2c1VkV4UE9IWTVhVWh2V0ZCd2VXRXdWVkpCTVZSamVGSnNTa2xEZEdWWU1tZEVaa2R3TkZoclVuZFNWR2hyTnk5d00zWllUVnA0ZUc0eWJtbGpjREl6UXpNeFpIZzRWelJNUjNCbGIxTnVTVk5qWTFKT2RqbFBXVVJHU1RWYVVtSk1NMU5KYldWelIwRkdlVFpHZFZVeU0ybEdaRWx3VGt3NGFXVjVVRlp3V2swMFIybHZZbFpVWW5aWWVFUkVSVU5RY1doVk1GUnlRbEZwWTJscGFWRnlNMGhhVTJWVGVGQnhXbTE1ZG1SUldEVmFZbkpPVTFGSFFXTkZhMDVNWlVKbFFWVjZjMUFyYjBkMkszRlBOMVpFUnpoVlVHOURiM2N4T1ZaYWVIQnVXRGROWldGWWRYTnRVMWt5YnpKcmJFUjBaR05oUmt4bU5FRTViMVZhY2pKck1sWjNkalJFWWxSbWJYWmhMMEpQWWpGaWRHcG9OeXRrUjBjdkt5dElhVE5NYmtvM2JXUkxSRGxpVFVGRFRsRnlkakpSWmtoMFdUWkRhRWRwYW0xUlFXSlRlRGRLUkhwcVl6UlpXa0pSU2xGQmMzUkRVSHB0WjJ4SFQwOUdLMGhvYmtselEycGFZblUwTHlzM0t6QTFXV0Z2UVdsYVJrTnNTamg0YldoeVlrOUJTR1JtUzNsSlNXUmFWVUk0Umk5dFdXNUdjR3hETVVvNFpXOXhjSHBGTlZNNGVDOVhaRTQwVkZkaGVXMVlhMUY2VlhOeFF5ODBRVGcyV21ONFZtWnBhWGhuVjBGME9UWmpjR1owVEc1SVRIcGxWWGxtWW1GV2MxVkpia1E1UVhsUE1Ia3ZhMlppYUVoMFRtMXhPVTl5T1RZdlpXYzNRMkZHWmxkRFdIWXhXa2RoUkV4NVducHdkVGxSTlhCdU9VUmpOVmRHVFhWWlMxazNhQ3RVYzJGV05UZ3hOVVl6U25acWRsVk1OMlJZUzNZeFRWcGxPWGg2T0hsUWVtZzRRbXhWWlVWS2JFcHVSa3RPWm5wNGRsTkRNakkzWldGTWQwOWFOVFEyY0VZdlVUTmhaVTVrVW5sVWJqbEZkM1kwWTFGak1UUkJSVkU5UEM5a2N6cFRhV2R1WVhSMWNtVldZV3gxWlQ0OFpITTZTMlY1U1c1bWJ6NDhaSE02V0RVd09VUmhkR0UrUEdSek9sZzFNRGxEWlhKMGFXWnBZMkYwWlQ1TlNVbEdXbnBEUTBFd0syZEJkMGxDUVdkSlNrRk5NVW8wY0RreFVGRmFNRTFCTUVkRFUzRkhVMGxpTTBSUlJVSkRkMVZCVFVWdmVFTjZRVXBDWjA1V1FrRlpWRUZyVWt4TlVrMTNDa1ZSV1VSV1VWRkpSRUZ3VkdJeU1XeE1WazR3V1ZoU2JFMVNaM2RHWjFsRVZsRlJTMFJCT1V4a2JVWnpZVmhTYkdSSVRrcFdRMEpDWTBaTmVFUkVRVXRDWjA1V1FrRk5UVUV6VGpBS1kzcEJaVVozTUhoUFZFRTFUVVJaZUUxVVRYbE5SRTVoUm5jd2VVOUVSWGhOYWtsNFRWUk5lVTFFVG1GTlJXOTRRM3BCU2tKblRsWkNRVmxVUVd0U1RFMVNUWGRGVVZsRVZsRlJTUXBFUVhCVVlqSXhiRXhXVGpCWldGSnNUVkpuZDBabldVUldVVkZMUkVFNVRHUnRSbk5oV0ZKc1pFaE9TbFpEUWtKalJrMTRSRVJCUzBKblRsWkNRVTFOUVROT01HTjZRME5CYVVsM0NrUlJXVXBMYjFwSmFIWmpUa0ZSUlVKQ1VVRkVaMmRKVUVGRVEwTkJaMjlEWjJkSlFrRk9TbGw0Y3poeFdrWkJZakZYTVhGaVIyNTJkRU5yV2tkVFRqQjRiMjQ1UlZVclIxQjZiRTBLWWtOSGRqRTVUR2xEZUdWbGN6UndaMFZKYVdKSE1FNHlkVmRtZUVOM05HMWxZVVZIWWtWSGNWUm1aM2s1S3lzeVp6RTBZVXhLSzNoemR6WjJSSFJIWlVnM2QwTjRWMUYyUVVKUWVBcGxOVkZFWlZnelJESmhaalJrUkdOSWNtcFlabVpKUVUxSk1UbFJNa0pZVFdWVVRtdFlXRU5XWkU5RVVFMU9aSGxHWm0xSFpIRmhhRkV3TWt4eGQyRk1VU3Q2TUZkcEszUlZObm8yQ2pGamMxRmljV05rWlU1ak1qUlFhRXBwZVdneE1tcDZXRXB6TTI5S2RUQkdLelp6ZFVwTVFVc3lOSEpZUlZGMVJFWTVZMkpFVTFKTk0yc3hRblI2WjJoNlZtTm9aVmRHTHk4NVNYRUtORWR6ZFM4dmMxWktaalpLYjNaVlZXNDNiVFpIV0ZWS0swazFRbmx5ZURNcmExRkdlR013WXpCQ2FYUlFSSFJQVEhKM2MwVlBkMlV2UTJkWEwwNW5hWHBOVmxkM2QyaGFXbWQ1WmdwS2J6VkNUMlUzYjBZd1FVYzRiakZWY0RoSlJsQkhZMVZ0Y1hGMWFITndNbm8yZG5Wck1IRkllVEJJVG01UWNsQTNkVXhaYVdGWmNucGpVV28yUldsWFdrWjZOMUZ4UWt4R2JGTTJDbVJYVlZNME9YbzFaMEZ6UkdsYVpqRlBNbmN6UzBKS1lVRkxVakphVHpKV0swWkpSazFtWWpGRVVHNXlkbW92VVRCbEx6aFBRbWxVWmtkeVV6UnBaMWhtWkdWRmJ6aDJOVFpFUTNBS1lrTm9laTl3WnpWME5HbEhiM1k0UmxCMlJYVmplSFExVFUxMVQzSXpOMnRYUWxWRWRFWjFURVpVTDJSd0swWk9iUzlXZEdkNFJqSnRXakJ5YTNSUGNteFZXVkpWYjFWa2JYRm1NQXB6VWpNME1FNURjSEJRTlc1c1VUTTRTVGR2T0Vwck1FUTFRemxRSzBWT1IzUnVRbE5JYTI5VFZuRmFiblJ2T1VONGRHVlFXVTFDUlVGSmVUWTRObEJJTUZkMmRWUjJjbmcwTlZoVkNubzJTbHBXY0hwelNYRTFibHBZY0hoVFMzUkpURXBvVG1oeloyeDFaWFp4TkdSNWVFRm5UVUpCUVVkcVZVUkNUMDFDTUVkQk1WVmtSR2RSVjBKQ1ZHOHpTek5GYVZZMlFuUmFOMlFLTjB0MFdrNVVlVXBVZDFac05HcEJaa0puVGxaSVUwMUZSMFJCVjJkQ1ZHOHpTek5GYVZZMlFuUmFOMlEzUzNSYVRsUjVTbFIzVm13MGFrRk5RbWRPVmtoU1RVVkNWRUZFUVZGSUx3cE5RVEJIUTFOeFIxTkpZak5FVVVWQ1EzZFZRVUUwU1VOQlVVTnFaWG80WkZsQksyWmlNR28zTjNaWVIyYzFRM0pKWTNWblYyRkxUalZzVTNscE5ISmljak5SUXpVM2JFSXhURk5qQ25WRVRIWnhZWGRZWTI4d1VrNW1jRVJIUW1ST1VuZFdTQ3NyV200MGJpOTNMM3A2TlN0ck5XazRhRTFhYjNsaGFrRjJUMjlJVjJGb2VXNU5UbHBOVXl0eU16QnFkRTB4Y3pKdlVUQUtUVzEzVFUxQ1pEaHNZM0p1YXpsQ09YSlVhbmRhWW00MVZUSkxLMFpuUm01clZ6UmtURTV3YldsNFNXVk5NM0JQUm5RMVdtbEhRbkJwYkRFNU15dDFiazFMVlVaT1dWbG5SR1pHYWdwTFQxQjFSVUUwTldsTWVIUjJiekZsYzJ0cFdqaGxWRGcxVDB4YU9YTlVOMU5oY2sxYU1uazBieTl1UzAxUGEyRnhaV0pDYkZnM1UzTTJiRTlEYkdkRFkyc3JibkZoVm5kbFdWRkxDbFJDUmxoek5WRlFSa2h3WmpGYVJuTnJSbVExVjAxUGVFRkdjV2xsVkhGVVdWWXZjSGhzTmpjNFFVWnlNMHAyYTJFMWJYWkJSRVJOU1ROSFZtUkVOM1pXUTBwWFZVWmhlRWRRUWswS1ZYbFRRMHQyVFhsR01UQjBZMUpIZEUxS2RXOHJZbXBzUlhwNE1YUjZia2hIWmpoVllVSkNOREZIU2xRdlRtMW1iSFZTVkdSdlNqQlNTRUZZVURSbWRYWlpOVEl2VHpOT1JFZEVSZ28wWWxGTE4yZFRNbG8xWVN0TFJVeFhhV1JDVDJ0eUwycEdhbFJ2YWpSTWQwSm5ZbUVyVEVkUVJUaHlNVlpIWkdSM2RsRlJXbFZaWjI5YVRUZHNlazFrTUVsVU5rbElNVU5YVEV3NUNrUkpNSHBTVXpkSVNsa3lNblE0VkdORGEwNWpjemN2UlN0WldWcEdlR2gyWTFoc1R6bFNjekJCUzFSWFprVmFOVmxFY2xOcGJEaEpTMkpNYm10dVJteHNkR0ZCTWtodE9WRlZXRkVLWkVkd1kxQnFjQ3RTV0ROUFdrTnVVbFZ1TVM5bWFWSkJVVTh5U3poWGVsWnlSR1JIV1RsaFJVNXVOMkpVU1daUksyOHdkRlo0UjBzM1ptOXFPRzA0YW1KeVZWRk5lamMyWWxkMlpRcHRVbFpLVjFBM1ZFdFBVMDVGY1dSSVZXUklOREZ2WWt0cmR6MDlQQzlrY3pwWU5UQTVRMlZ5ZEdsbWFXTmhkR1UrUEM5a2N6cFlOVEE1UkdGMFlUNDhMMlJ6T2t0bGVVbHVabTgrUEM5a2N6cFRhV2R1WVhSMWNtVStQSE5oYld3eU9sTjFZbXBsWTNRK1BITmhiV3d5T2s1aGJXVkpSQ0JHYjNKdFlYUTlJblZ5YmpwdllYTnBjenB1WVcxbGN6cDBZenBUUVUxTU9qRXVNVHB1WVcxbGFXUXRabTl5YldGME9uVnVjM0JsWTJsbWFXVmtJaUJPWVcxbFVYVmhiR2xtYVdWeVBTSm9kSFJ3T2k4dmQzZDNMbTVsZUhSemRHVndZMmwwYVhwbGJpNWtheTl6ZEhNaVBrTk9QVzFsWkdOdmJYTjVjM1JsYlhWelpYSXNUejFKYm5SbGNtNWxkQ0JYYVdSbmFYUnpJRkIwZVNCTWRHUXNVMVE5VTI5dFpTMVRkR0YwWlN4RFBVUkxQQzl6WVcxc01qcE9ZVzFsU1VRK1BITmhiV3d5T2xOMVltcGxZM1JEYjI1bWFYSnRZWFJwYjI0Z1RXVjBhRzlrUFNKMWNtNDZiMkZ6YVhNNmJtRnRaWE02ZEdNNlUwRk5URG95TGpBNlkyMDZhRzlzWkdWeUxXOW1MV3RsZVNJK1BITmhiV3d5T2xOMVltcGxZM1JEYjI1bWFYSnRZWFJwYjI1RVlYUmhJSGh6YVRwMGVYQmxQU0p6WVcxc01qcExaWGxKYm1adlEyOXVabWx5YldGMGFXOXVSR0YwWVZSNWNHVWlQanhrY3pwTFpYbEpibVp2SUhodGJHNXpPbVJ6UFNKb2RIUndPaTh2ZDNkM0xuY3pMbTl5Wnk4eU1EQXdMekE1TDNodGJHUnphV2NqSWo0OFpITTZTMlY1Vm1Gc2RXVStQR1J6T2xKVFFVdGxlVlpoYkhWbFBqeGtjenBOYjJSMWJIVnpQbkpZUVhCNGVHcERWMnh6UldabFMyZFZVRTlzTVcxS1F6bGhjV3RyVjI5dmVWVm5UMVVyUzNOeVNEbHhVa052U3psNFZtUkpOMWxLWldKM2NqVXJWRXAwUW1KWGEwdHJkVVE1TWpZS1UwMTRTbFl4VEZrMlNWUTRkRU5tYkc5dFNXdzBSVFZKV21SU1dsQmphVEZPTnpGc1VVUldObE5tVG5WSFVFaE9jRVp3VEhOelpGTlpNelFyZERRdmRuVkhaVlJhTW14S1FqVkpVQW8wYzBSMmFrRjRTaXR1V0VWRFkwaHRZM1Z3UlVWUmRUTjNTVEp1YVdwalYydzBhRkpTVTJSb1ZYVkxSRUl2UVdsaFduWnpVRXRqWkVacU5GZFViRkprWlhkS1V6UjJOVzB4YTJoM0NtTmxObHBxTVdwM05rNDNVRk5SVUVoaGFYTkplSEY0TWxOTlNIWkxhV1Z3VUhWRlUyZEZjSEZRSzNOSFVtRk1Na1ZUU2xkMVFqRnJWSE5PU0cxbGNqWmpTaXRpWVM5d2RrcDVNM2dLY21GWk4yMXlaMUoyTDNwWFlTczJUMlk1VEZOV2R6Sm9aa1o0TTNCRmFrSm5XVWhvYUhjOVBUd3ZaSE02VFc5a2RXeDFjejQ4WkhNNlJYaHdiMjVsYm5RK1FWRkJRand2WkhNNlJYaHdiMjVsYm5RK1BDOWtjenBTVTBGTFpYbFdZV3gxWlQ0OEwyUnpPa3RsZVZaaGJIVmxQand2WkhNNlMyVjVTVzVtYno0OEwzTmhiV3d5T2xOMVltcGxZM1JEYjI1bWFYSnRZWFJwYjI1RVlYUmhQand2YzJGdGJESTZVM1ZpYW1WamRFTnZibVpwY20xaGRHbHZiajQ4TDNOaGJXd3lPbE4xWW1wbFkzUStQSE5oYld3eU9rTnZibVJwZEdsdmJuTWdUbTkwUW1WbWIzSmxQU0l5TURJd0xUQXpMVEUzVkRFeU9qSTJPakV5TGprME1Gb2lJRTV2ZEU5dVQzSkJablJsY2owaU1qQXlNQzB3TXkweE4xUXhNem94TWpvMU1pNDVOREJhSWo0OGMyRnRiREk2UVhWa2FXVnVZMlZTWlhOMGNtbGpkR2x2Ymo0OGMyRnRiREk2UVhWa2FXVnVZMlUrZFhKdU9tdHBkRHAwWlhOMFlUcHpaWEoyYVdObFlUd3ZjMkZ0YkRJNlFYVmthV1Z1WTJVK1BDOXpZVzFzTWpwQmRXUnBaVzVqWlZKbGMzUnlhV04wYVc5dVBqd3ZjMkZ0YkRJNlEyOXVaR2wwYVc5dWN6NDhjMkZ0YkRJNlFYUjBjbWxpZFhSbFUzUmhkR1Z0Wlc1MFBqeHpZVzFzTWpwQmRIUnlhV0oxZEdVZ1RtRnRaVDBpWkdzNmJtVjRkSE4wWlhCamFYUnBlbVZ1T21GMGRISnBZblYwWlRwcGRDMXplWE4wWlcwaUlFNWhiV1ZHYjNKdFlYUTlJblZ5YmpwdllYTnBjenB1WVcxbGN6cDBZenBUUVUxTU9qSXVNRHBoZEhSeWJtRnRaUzFtYjNKdFlYUTZZbUZ6YVdNaVBqeHpZVzFzTWpwQmRIUnlhV0oxZEdWV1lXeDFaU0I0YzJrNmRIbHdaVDBpZUhNNmMzUnlhVzVuSWo1dFpXUmpiMjF6ZVhOMFpXMTFjMlZ5UEM5ellXMXNNanBCZEhSeWFXSjFkR1ZXWVd4MVpUNDhMM05oYld3eU9rRjBkSEpwWW5WMFpUNDhjMkZ0YkRJNlFYUjBjbWxpZFhSbElFNWhiV1U5SW5SbGMzUTZkR1Z6ZENJZ1RtRnRaVVp2Y20xaGREMGlkWEp1T205aGMybHpPbTVoYldWek9uUmpPbE5CVFV3Nk1pNHdPbUYwZEhKdVlXMWxMV1p2Y20xaGREcGlZWE5wWXlJK1BITmhiV3d5T2tGMGRISnBZblYwWlZaaGJIVmxJSGh6YVRwMGVYQmxQU0o0Y3pwemRISnBibWNpUG1GMWRHOTJZV3gxWlRFOEwzTmhiV3d5T2tGMGRISnBZblYwWlZaaGJIVmxQand2YzJGdGJESTZRWFIwY21saWRYUmxQanh6WVcxc01qcEJkSFJ5YVdKMWRHVWdUbUZ0WlQwaWRHVnpkRHAwWlhOMElpQk9ZVzFsUm05eWJXRjBQU0oxY200NmIyRnphWE02Ym1GdFpYTTZkR002VTBGTlREb3lMakE2WVhSMGNtNWhiV1V0Wm05eWJXRjBPbUpoYzJsaklqNDhjMkZ0YkRJNlFYUjBjbWxpZFhSbFZtRnNkV1VnZUhOcE9uUjVjR1U5SW5oek9uTjBjbWx1WnlJK1lYVjBiM1poYkhWbE1qd3ZjMkZ0YkRJNlFYUjBjbWxpZFhSbFZtRnNkV1UrUEM5ellXMXNNanBCZEhSeWFXSjFkR1UrUEM5ellXMXNNanBCZEhSeWFXSjFkR1ZUZEdGMFpXMWxiblErUEM5ellXMXNNanBCYzNObGNuUnBiMjQrIiwiVGltZXN0YW1wIjoiMjAyMC0wMy0xN1QxMzoxMjo1Mi45NFoiLCJIYXNoIjoiUGRTQ2RlbEdiRkdtZU9qbWtwVmhHUT09IiwiVXNlckF0dHJpYnV0ZXMiOnsiZGs6bmV4dHN0ZXBjaXRpemVuOmF0dHJpYnV0ZTppdC1zeXN0ZW0iOlsibWVkY29tc3lzdGVtdXNlciJdLCJ0ZXN0OnRlc3QiOlsiYXV0b3ZhbHVlMSIsImF1dG92YWx1ZTIiXX0sIlNlc3Npb25BdHRyaWJ1dGVzIjp7fSwiQ2xpZW50Q2VydEhhc2giOiJmMGU2YjY5YmVkNDQ1NTU4NWIzYjRhNDAwMjdiZWU2MjRmZWY4ZjA2In0=";
		
		// When
		SessionData sd = subject.parseSessionDataValue(headerValue);
		
		// Then
		Assert.assertNotNull("Expected a sessiondata object", sd);
		Map<String, List<String>> userAttributes = sd.getUserAttributes();
		Assert.assertNotNull("Exepcted userattributes", userAttributes);
		Assert.assertTrue(userAttributes.containsKey("test:test"));
		Assert.assertEquals(2, userAttributes.get("test:test").size());
		Assert.assertTrue(userAttributes.get("test:test").contains("autovalue1"));
		Assert.assertTrue(userAttributes.get("test:test").contains("autovalue2"));
	}
	
	@Test
	public void testParseSessionDataValueFromBase64EncodedStringNotSessionData() {
		
		// Given
		String headerValue = "eyAiaGVqIjogImt1ayIsICJibGEiOiAiMTIzNCIgfQo=";
		
		// When
		SessionData sd = subject.parseSessionDataValue(headerValue);
		
		// Then
		Assert.assertNull("Expected no sessiondata object", sd);
	}
	
	@Test
	public void testParseSessionDataValueOnNonBase64EncodedInput() {
		// Given
		String headerValue = "Not base64 encoded";
		
		// When
		SessionData sd = subject.parseSessionDataValue(headerValue);
		
		// Then
		Assert.assertNull("Expected no sessiondata object", sd);
	}
}