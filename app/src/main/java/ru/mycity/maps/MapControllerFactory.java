package ru.mycity.maps;

public class MapControllerFactory
{

    public MapControllerFactory()
    {
    }

    public static IMapController newInstance(IMapClient mapClient, boolean clustering)
    {
        /*
        if (null != settings)
        {
            final int val = OptionsUtils.getInt(settings, "map_type", 1);
            if (1 == val)
                return new OpenStreetMapController(mapClient);
        }*/

        if (clustering)
            return new GoogleMapsControllerClustering(mapClient);


        return new GoogleMapsController(mapClient);
    }

}
