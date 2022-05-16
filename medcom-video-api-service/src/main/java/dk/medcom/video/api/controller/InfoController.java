package dk.medcom.video.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class InfoController {
	@Autowired
	private List<InfoContributor> infoContributors;

	@RequestMapping(value = "/info")
	public Map<String, Object> getDummies() {
		Info.Builder builder = new Info.Builder();

		infoContributors.forEach(x -> x.contribute(builder));

		Info build = builder.build();
		return build.getDetails();
	}
}
