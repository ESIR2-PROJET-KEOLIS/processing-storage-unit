package com.treatmentunit.restservice;

import com.treatmentunit.database.DatabaseBinding;
import com.treatmentunit.simulation.OptimisationAndFormating;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class APIController {

    private static final String template = "Hello %s !";
    private static final AtomicLong counter = new AtomicLong();

    OptimisationAndFormating optAndForm = new OptimisationAndFormating();

    @GetMapping("/test")
    public String greeting(@RequestParam(value = "id") int id) throws IOException {

        DatabaseBinding databaseBinding = new DatabaseBinding();
        databaseBinding.connectToSqlSocket();

        StringBuilder sb = new StringBuilder();
        BufferedReader reader = Files.newBufferedReader(Paths.get("C:\\Users\\1234Y\\OneDrive\\Documents\\DossierEtudes2\\PROJ-SI\\rest-service\\rest-service\\src\\main\\java\\com\\treatmentunit\\restservice\\coords_to_test.txt"));

        String test_all_coords = "[[-1.621787, 48.13471], [-1.622318, 48.134506], [-1.622695, 48.134831], [-1.623008, 48.134671], [-1.623195, 48.134611], [-1.623456, 48.134586], [-1.623728, 48.134566], [-1.623881, 48.1345], [-1.623997, 48.134341], [-1.624158, 48.133961], [-1.624278, 48.133664], [-1.624346, 48.133519], [-1.624356, 48.133382], [-1.624293, 48.13326], [-1.624146, 48.133123], [-1.624088, 48.133001], [-1.624211, 48.132642], [-1.624252, 48.132486], [-1.624646, 48.131406], [-1.624779, 48.131056], [-1.624769, 48.131014], [-1.624818, 48.130851], [-1.624848, 48.130807], [-1.62491, 48.130779], [-1.625239, 48.13071], [-1.625637, 48.130654], [-1.626084, 48.130562], [-1.626657, 48.130415], [-1.627736, 48.130151], [-1.630632, 48.12973], [-1.630719, 48.129706], [-1.632195, 48.129141], [-1.632825, 48.128883], [-1.632942, 48.128827], [-1.633036, 48.128768], [-1.6331, 48.128744], [-1.633121, 48.128757], [-1.633147, 48.128765], [-1.633176, 48.128766], [-1.633203, 48.128762], [-1.633228, 48.128753], [-1.633247, 48.128739], [-1.633258, 48.128721], [-1.633465, 48.128773], [-1.633914, 48.128838], [-1.634193, 48.128866], [-1.634507, 48.128889], [-1.635061, 48.128901], [-1.635058, 48.128919], [-1.635065, 48.128937], [-1.635079, 48.128952], [-1.635101, 48.128962], [-1.635127, 48.128967], [-1.635153, 48.128966], [-1.635178, 48.128959], [-1.635197, 48.128947], [-1.635209, 48.128931], [-1.635213, 48.128913], [-1.635206, 48.128895], [-1.635192, 48.128881], [-1.63517, 48.12887], [-1.635216, 48.128836], [-1.635333, 48.128655], [-1.635395, 48.12858], [-1.635492, 48.128501], [-1.635587, 48.128448], [-1.635662, 48.128415], [-1.635784, 48.12838], [-1.635942, 48.128355], [-1.63686, 48.128278], [-1.637526, 48.128228], [-1.637826, 48.128216], [-1.638156, 48.128214], [-1.638623, 48.128233], [-1.639123, 48.128261], [-1.639228, 48.128259], [-1.639315, 48.128249], [-1.639396, 48.128233], [-1.63947, 48.128213], [-1.639572, 48.128181], [-1.639643, 48.128148], [-1.639701, 48.128112], [-1.639761, 48.12807], [-1.639825, 48.128019], [-1.640154, 48.127583], [-1.640482, 48.127163], [-1.64056, 48.127083], [-1.640624, 48.127028], [-1.640782, 48.126913], [-1.640927, 48.126827], [-1.64106, 48.126763], [-1.641196, 48.126708], [-1.641213, 48.126726], [-1.641237, 48.126741], [-1.641267, 48.12675], [-1.6413, 48.126752], [-1.641332, 48.126747], [-1.64136, 48.126736], [-1.641382, 48.12672], [-1.641395, 48.1267], [-1.641398, 48.126678], [-1.641391, 48.126656], [-1.641374, 48.126638], [-1.64135, 48.126623], [-1.64132, 48.126614], [-1.641379, 48.12654], [-1.641786, 48.125963], [-1.641966, 48.125745], [-1.642129, 48.125558], [-1.64224, 48.125455], [-1.642468, 48.125284], [-1.642688, 48.125142], [-1.644686, 48.123975], [-1.644937, 48.123844], [-1.645294, 48.123697], [-1.645766, 48.123515], [-1.648566, 48.122397], [-1.650992, 48.122206], [-1.651, 48.122233], [-1.651016, 48.122258], [-1.651042, 48.12228], [-1.651074, 48.122297], [-1.651111, 48.122308], [-1.651151, 48.122313], [-1.651192, 48.122311], [-1.651231, 48.122303], [-1.651266, 48.122289], [-1.651295, 48.122269], [-1.651316, 48.122246], [-1.651328, 48.12222], [-1.651331, 48.122193], [-1.651323, 48.122166], [-1.651314, 48.122152], [-1.654371, 48.121201], [-1.654377, 48.12121], [-1.654404, 48.121233], [-1.654437, 48.12125], [-1.654476, 48.121262], [-1.654518, 48.121267], [-1.654561, 48.121265], [-1.654602, 48.121257], [-1.654638, 48.121242], [-1.654676, 48.121213], [-1.654818, 48.121281], [-1.659525, 48.123436], [-1.659618, 48.123525], [-1.659982, 48.12327], [-1.661258, 48.122515], [-1.661749, 48.122273], [-1.662658, 48.121838], [-1.662726, 48.121788], [-1.663989, 48.12118], [-1.665355, 48.120351], [-1.666298, 48.119784], [-1.666442, 48.119708], [-1.667424, 48.119297], [-1.668957, 48.118489], [-1.669058, 48.118451], [-1.669193, 48.11841], [-1.669341, 48.118381], [-1.670147, 48.118318], [-1.672197, 48.118199], [-1.672359, 48.118183], [-1.673304, 48.117922], [-1.673495, 48.117777], [-1.673657, 48.11754], [-1.673897, 48.117147], [-1.674018, 48.116934], [-1.674136, 48.11645], [-1.674141, 48.116359], [-1.674179, 48.116054], [-1.674377, 48.115103], [-1.674425, 48.1148], [-1.674475, 48.114558], [-1.675078, 48.113596], [-1.6751, 48.113523], [-1.675236, 48.113193], [-1.675281, 48.113054], [-1.67532, 48.112848], [-1.675274, 48.112632], [-1.675145, 48.112408], [-1.675112, 48.111948], [-1.674773, 48.111372], [-1.674338, 48.110658], [-1.674193, 48.110202], [-1.674098, 48.109875], [-1.675489, 48.109871], [-1.676378, 48.109864], [-1.677981, 48.10986], [-1.67798, 48.109334], [-1.677915, 48.109335], [-1.677826, 48.109355], [-1.677834, 48.108615], [-1.677838, 48.107388], [-1.677737, 48.107334], [-1.677737, 48.106445], [-1.677681, 48.105949], [-1.677595, 48.105388], [-1.677527, 48.105122], [-1.677324, 48.10445], [-1.677096, 48.103867], [-1.676934, 48.103493], [-1.673408, 48.10393], [-1.673323, 48.103961], [-1.67321, 48.103996], [-1.673054, 48.104055], [-1.672724, 48.104194], [-1.672528, 48.104265], [-1.672395, 48.10428], [-1.671587, 48.104105], [-1.671492, 48.104156], [-1.668619, 48.104508], [-1.666144, 48.104824], [-1.665988, 48.104762], [-1.665762, 48.104745], [-1.665128, 48.10472], [-1.664777, 48.104716], [-1.664644, 48.104718], [-1.663787, 48.104197], [-1.662832, 48.1036], [-1.662718, 48.103562], [-1.660884, 48.102469], [-1.660157, 48.102003], [-1.659871, 48.101873], [-1.659745, 48.101824], [-1.659616, 48.101784], [-1.659468, 48.101755], [-1.659327, 48.10174], [-1.659207, 48.101742], [-1.659103, 48.101753], [-1.658865, 48.101798], [-1.65785, 48.102058], [-1.657763, 48.102068], [-1.657696, 48.102071], [-1.657577, 48.102067], [-1.657487, 48.102058], [-1.656145, 48.101811], [-1.655171, 48.101601], [-1.654495, 48.101473], [-1.65274, 48.101213], [-1.652426, 48.101157], [-1.651642, 48.100985], [-1.651184, 48.100827], [-1.650856, 48.100706], [-1.649922, 48.100394], [-1.648892, 48.100064], [-1.647407, 48.099615], [-1.645108, 48.098899], [-1.64487, 48.098818], [-1.644573, 48.098709], [-1.64427, 48.098575], [-1.643999, 48.098444], [-1.643785, 48.09833], [-1.643259, 48.098018], [-1.642097, 48.097348], [-1.641088, 48.09682], [-1.640832, 48.09668], [-1.63936, 48.095891], [-1.639191, 48.095809], [-1.638522, 48.095514], [-1.637697, 48.095212], [-1.634345, 48.094082], [-1.629979, 48.092619], [-1.629644, 48.092454], [-1.628086, 48.091905], [-1.626708, 48.091431], [-1.62637, 48.091308], [-1.626141, 48.091198], [-1.625898, 48.091062], [-1.625716, 48.090937], [-1.625493, 48.090761], [-1.625347, 48.090611], [-1.625165, 48.090478], [-1.624791, 48.090554], [-1.62458, 48.090588], [-1.624517, 48.090585], [-1.62432, 48.09053], [-1.624083, 48.090423], [-1.623552, 48.090173], [-1.62356, 48.090147], [-1.623562, 48.090128], [-1.623545, 48.090097], [-1.623526, 48.090079], [-1.623512, 48.090072], [-1.623482, 48.090063], [-1.62345, 48.090059], [-1.623417, 48.090062], [-1.623387, 48.090071], [-1.623375, 48.090078], [-1.623121, 48.089967], [-1.622648, 48.089738], [-1.621581, 48.089203], [-1.621156, 48.088996], [-1.620793, 48.088833], [-1.620251, 48.088699], [-1.619801, 48.088569], [-1.618765, 48.088331], [-1.618212, 48.088236], [-1.617965, 48.088213], [-1.617705, 48.088146], [-1.617327, 48.088027], [-1.617176, 48.087985], [-1.617076, 48.087992], [-1.617057, 48.087949], [-1.617033, 48.087875], [-1.616773, 48.087596], [-1.616588, 48.087371], [-1.616446, 48.087217], [-1.616387, 48.087165], [-1.616191, 48.087004], [-1.615802, 48.086708], [-1.615215, 48.086267], [-1.614616, 48.085843], [-1.614518, 48.085741], [-1.614531, 48.085724], [-1.614534, 48.085706], [-1.614527, 48.085687], [-1.614512, 48.085671], [-1.614489, 48.08566], [-1.614462, 48.085655], [-1.614434, 48.085656], [-1.614408, 48.085663], [-1.614387, 48.085676], [-1.614375, 48.085693], [-1.614371, 48.085712], [-1.614298, 48.085724], [-1.614157, 48.085724], [-1.613364, 48.085683], [-1.613006, 48.08566], [-1.612751, 48.085629], [-1.612516, 48.085587], [-1.611755, 48.085413], [-1.611166, 48.085273], [-1.610501, 48.085107], [-1.61025, 48.085048], [-1.609613, 48.08488], [-1.609625, 48.084857], [-1.609627, 48.084832], [-1.609619, 48.084807], [-1.609601, 48.084784], [-1.609575, 48.084765], [-1.609553, 48.084756], [-1.609508, 48.084746], [-1.609481, 48.084745], [-1.609435, 48.084751], [-1.609411, 48.084759], [-1.609387, 48.084773], [-1.609367, 48.084789], [-1.609351, 48.084813], [-1.609346, 48.084841], [-1.60935, 48.084861], [-1.609363, 48.084883], [-1.609378, 48.084899], [-1.60939, 48.084906], [-1.609318, 48.084937], [-1.609275, 48.085004], [-1.60922, 48.085124], [-1.609196, 48.085236], [-1.609161, 48.085431], [-1.609124, 48.085435], [-1.609093, 48.085442], [-1.609055, 48.085459], [-1.609025, 48.085483], [-1.609006, 48.085514], [-1.609001, 48.085547], [-1.609011, 48.085579], [-1.609035, 48.085607], [-1.60907, 48.08563], [-1.60893, 48.085684], [-1.608677, 48.08571], [-1.606834, 48.085746], [-1.605823, 48.085775], [-1.604604, 48.085797], [-1.604442, 48.085743], [-1.603964, 48.085545]]";

        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        return optAndForm.getOutput(test_all_coords);

        }

    /*
    @GetMapping("/greeting")
    public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }

    @GetMapping("/greeting2")
    public Greeting greeting2(@RequestParam(value = "name") String name) {
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }
    */
}